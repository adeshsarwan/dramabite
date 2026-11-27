package com.dramabite.thebes.app.billing

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.dramabite.thebes.app.ads.PremiumPlanManager
import com.dramabite.thebes.app.ads.RewardAdManager
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.dramabite.thebes.app.utils.coins.CoinsManager
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

/**
 * Google Play Billing for coin packs and membership passes.
 *
 * All products are one-time, consumable in-app products (no auto-renewing subscriptions):
 *  coins_pack_1..4      -> coins (+ extra coins) from the Remote Config plan JSON
 *  membership_weekly    -> 7-day premium pass
 *  membership_monthly   -> 30-day premium pass
 *  membership_yearly    -> 365-day premium pass
 * Create these product IDs in Play Console -> Monetize with Play -> One-time products.
 *
 * Each purchase token is granted exactly once (tracked in SharedPreferences), then consumed.
 * Purchases left unconsumed (app killed, pending payment completed later) are granted by
 * [restore], which runs on every connection.
 */
object PlayBilling {
    private const val TAG = "PlayBilling"
    private const val PREFS = "play_billing"
    private const val KEY_GRANTED = "granted_tokens"

    const val COINS_PACK_1 = "coins_pack_1"
    const val COINS_PACK_2 = "coins_pack_2"
    const val COINS_PACK_3 = "coins_pack_3"
    const val COINS_PACK_4 = "coins_pack_4"
    const val MEMBERSHIP_WEEKLY = "membership_weekly"
    const val MEMBERSHIP_MONTHLY = "membership_monthly"
    const val MEMBERSHIP_YEARLY = "membership_yearly"

    private val ALL_PRODUCTS = listOf(
        COINS_PACK_1, COINS_PACK_2, COINS_PACK_3, COINS_PACK_4,
        MEMBERSHIP_WEEKLY, MEMBERSHIP_MONTHLY, MEMBERSHIP_YEARLY
    )

    /** Result of a purchase started with [purchase]. Callbacks run on the main thread. */
    interface Listener {
        /** Payment went through. The caller grants coins / membership and updates its UI. */
        fun onPurchased(purchaseType: Int)
        fun onCancelled()
        fun onFailed(message: String)
        /** Payment is pending (e.g. cash at a store); it is granted later by [restore]. */
        fun onPending() {}
    }

    private val main = Handler(Looper.getMainLooper())
    private var appContext: Context? = null
    private var client: BillingClient? = null
    private val details = HashMap<String, ProductDetails>()
    private var activeListener: Listener? = null
    private var activePurchaseType = 0
    private val onReady = ArrayList<() -> Unit>()

    @JvmStatic
    fun productIdFor(purchaseType: Int): String? = when (purchaseType) {
        RewardAdManager.PURCHASE_TYPE_COINS_PACK_1 -> COINS_PACK_1
        RewardAdManager.PURCHASE_TYPE_COINS_PACK_2 -> COINS_PACK_2
        RewardAdManager.PURCHASE_TYPE_COINS_PACK_3 -> COINS_PACK_3
        RewardAdManager.PURCHASE_TYPE_COINS_PACK_4 -> COINS_PACK_4
        RewardAdManager.PURCHASE_TYPE_WEEKLY -> MEMBERSHIP_WEEKLY
        RewardAdManager.PURCHASE_TYPE_MONTHLY -> MEMBERSHIP_MONTHLY
        RewardAdManager.PURCHASE_TYPE_YEARLY -> MEMBERSHIP_YEARLY
        else -> null
    }

    private fun purchaseTypeFor(productId: String): Int = when (productId) {
        COINS_PACK_1 -> RewardAdManager.PURCHASE_TYPE_COINS_PACK_1
        COINS_PACK_2 -> RewardAdManager.PURCHASE_TYPE_COINS_PACK_2
        COINS_PACK_3 -> RewardAdManager.PURCHASE_TYPE_COINS_PACK_3
        COINS_PACK_4 -> RewardAdManager.PURCHASE_TYPE_COINS_PACK_4
        MEMBERSHIP_WEEKLY -> RewardAdManager.PURCHASE_TYPE_WEEKLY
        MEMBERSHIP_MONTHLY -> RewardAdManager.PURCHASE_TYPE_MONTHLY
        MEMBERSHIP_YEARLY -> RewardAdManager.PURCHASE_TYPE_YEARLY
        else -> 0
    }

    private val purchasesListener = PurchasesUpdatedListener { result, purchases ->
        val listener = activeListener
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { handlePurchase(it, listener) }
                    ?: post { listener?.onFailed("No purchase returned") }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> post { listener?.onCancelled() }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // A previous purchase of this product was never consumed: finish it now.
                restore(null)
                post { listener?.onFailed("Finishing a previous purchase, please try again") }
            }
            else -> post { listener?.onFailed(result.debugMessage.ifEmpty { "Error ${result.responseCode}" }) }
        }
        if (result.responseCode != BillingClient.BillingResponseCode.OK) clearActive()
    }

    /** Connects to Play and prefetches product prices. Safe to call repeatedly. */
    @JvmStatic
    fun init(context: Context) {
        if (appContext == null) appContext = context.applicationContext
        if (client?.isReady == true) return
        if (client == null) {
            client = BillingClient.newBuilder(appContext!!)
                .setListener(purchasesListener)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
                )
                .enableAutoServiceReconnection()
                .build()
        }
        client?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    loadProducts()
                    restore(null)
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.responseCode} ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun loadProducts() {
        val products = ALL_PRODUCTS.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        client?.queryProductDetailsAsync(params) { result, queryResult ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                synchronized(details) {
                    queryResult.productDetailsList.forEach { details[it.productId] = it }
                }
                Log.d(TAG, "Loaded ${details.size} products")
            } else {
                Log.w(TAG, "Product query failed: ${result.debugMessage}")
            }
            val callbacks = synchronized(onReady) { onReady.toList().also { onReady.clear() } }
            post { callbacks.forEach { it() } }
        }
    }

    /** Runs [block] on the main thread once product prices are loaded (or failed to load). */
    @JvmStatic
    fun whenPricesReady(context: Context, block: Runnable) {
        init(context)
        if (details.isNotEmpty()) {
            post { block.run() }
        } else {
            synchronized(onReady) { onReady.add { block.run() } }
        }
    }

    /** Localised Play price, e.g. "₹99.00", or null if the product is not loaded yet. */
    @JvmStatic
    fun formattedPrice(purchaseType: Int): String? {
        val id = productIdFor(purchaseType) ?: return null
        return details[id]?.oneTimePurchaseOfferDetails?.formattedPrice
    }

    /** Play price as a number in the user's currency, or 0.0 if unknown. */
    @JvmStatic
    fun priceAmount(purchaseType: Int): Double {
        val id = productIdFor(purchaseType) ?: return 0.0
        val micros = details[id]?.oneTimePurchaseOfferDetails?.priceAmountMicros ?: return 0.0
        return micros / 1_000_000.0
    }

    /** Opens the Google Play purchase sheet for [purchaseType]. */
    @JvmStatic
    fun purchase(activity: Activity, purchaseType: Int, listener: Listener) {
        init(activity)
        val productId = productIdFor(purchaseType)
        val product = productId?.let { details[it] }
        if (client?.isReady != true || product == null) {
            post { listener.onFailed("Store not ready, please try again in a moment") }
            if (client?.isReady == true) loadProducts()
            return
        }
        val uid = FirebaseAuth.getInstance().uid
        activeListener = listener
        activePurchaseType = purchaseType
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(product)
            .build()
        val flowBuilder = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
        if (uid != null) flowBuilder.setObfuscatedAccountId(uid.take(64))
        val result = client!!.launchBillingFlow(activity, flowBuilder.build())
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            clearActive()
            post { listener.onFailed(result.debugMessage.ifEmpty { "Error ${result.responseCode}" }) }
        }
    }

    private fun handlePurchase(purchase: Purchase, listener: Listener?) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {
                val productId = purchase.products.firstOrNull() ?: return
                val type = purchaseTypeFor(productId)
                if (!markGranted(purchase.purchaseToken)) {
                    consume(purchase)
                    return
                }
                if (listener != null && type == activePurchaseType) {
                    post { listener.onPurchased(type) }
                    clearActive()
                } else {
                    grantCentrally(type)
                }
                consume(purchase)
            }
            Purchase.PurchaseState.PENDING -> {
                post { listener?.onPending() }
                clearActive()
            }
            else -> {}
        }
    }

    /**
     * Grants and consumes purchases that were paid but never consumed (app closed mid-flow,
     * delayed payment methods). Needs a signed-in user, because coins and membership are
     * stored per user in Firestore. Pass a context to (re)connect first.
     */
    @JvmStatic
    fun restore(context: Context?) {
        if (context != null) init(context)
        val c = client ?: return
        if (!c.isReady) return
        if (FirebaseAuth.getInstance().uid == null) return
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        c.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryPurchasesAsync
            purchases.filter { it.purchaseState == Purchase.PurchaseState.PURCHASED }
                .forEach { handlePurchase(it, null) }
        }
    }

    private fun grantCentrally(type: Int) {
        val ctx = appContext ?: return
        val uid = FirebaseAuth.getInstance().uid ?: return
        when (type) {
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_1,
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_2,
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_3,
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_4 -> {
                val coins = coinsForPack(type)
                if (coins > 0) CoinsManager.addCoins(uid, coins, null, { e -> Log.w(TAG, "addCoins failed", e) })
            }
            RewardAdManager.PURCHASE_TYPE_WEEKLY -> PremiumPlanManager.savePremiumPurchaseToFirestore(
                ctx, true, false, priceAmount(type), PremiumPlanManager.DURATION_WEEKLY_MS, null
            )
            RewardAdManager.PURCHASE_TYPE_MONTHLY -> PremiumPlanManager.savePremiumPurchaseToFirestore(
                ctx, false, false, priceAmount(type), PremiumPlanManager.DURATION_MONTHLY_MS, null
            )
            RewardAdManager.PURCHASE_TYPE_YEARLY -> PremiumPlanManager.savePremiumPurchaseToFirestore(
                ctx, false, true, priceAmount(type), PremiumPlanManager.DURATION_YEARLY_MS, null
            )
        }
    }

    /** Coins (+ extra coins) for a pack, from the same Remote Config plan JSON the UI shows. */
    @JvmStatic
    fun coinsForPack(type: Int): Long {
        val key = when (type) {
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_1 -> "standard_plan_1"
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_2 -> "standard_plan_2"
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_3 -> "standard_plan_3"
            RewardAdManager.PURCHASE_TYPE_COINS_PACK_4 -> "standard_plan_4"
            else -> return 0L
        }
        return try {
            val country = ControlPreference.getCountryName()
            val json = JSONObject(
                if (country.equals("IN", ignoreCase = true)) ControlPreference.getInrPlans()
                else ControlPreference.getUsdPlans()
            )
            val plan = json.getJSONObject(key)
            (plan.getInt("coins") + plan.optInt("extra_coins", 0)).toLong()
        } catch (e: Exception) {
            0L
        }
    }

    private fun consume(purchase: Purchase) {
        val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        client?.consumeAsync(params) { result, _ ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Consume failed: ${result.debugMessage}")
                // Acknowledge so Play does not refund it; it is consumed on the next restore.
                if (!purchase.isAcknowledged) {
                    client?.acknowledgePurchase(
                        AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken).build()
                    ) { }
                }
            }
        }
    }

    /** Returns true the first time a token is seen, false if it was already granted. */
    @Synchronized
    private fun markGranted(token: String): Boolean {
        val prefs = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE) ?: return true
        val granted = prefs.getStringSet(KEY_GRANTED, emptySet())!!.toMutableSet()
        if (!granted.add(token)) return false
        prefs.edit().putStringSet(KEY_GRANTED, granted).apply()
        return true
    }

    private fun clearActive() {
        activeListener = null
        activePurchaseType = 0
    }

    private fun post(block: () -> Unit) {
        main.post(block)
    }
}

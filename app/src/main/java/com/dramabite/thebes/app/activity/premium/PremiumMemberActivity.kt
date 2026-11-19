package com.dramabite.thebes.app.activity.premium

import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.dramabite.thebes.app.R
import com.dramabite.thebes.app.activity.main.base.BaseOtherActivity
import com.dramabite.thebes.app.adapter.ShortDramaAdapter
import com.dramabite.thebes.app.ads.PremiumPlanManager
import com.dramabite.thebes.app.ads.PremiumPlanManager.SavePremiumCallback
import com.dramabite.thebes.app.ads.RewardAdManager
import com.dramabite.thebes.app.applicationPreference.ControlPreference
import com.dramabite.thebes.app.databinding.ActivityPremiumMemberBinding
import com.dramabite.thebes.app.mvvmRepo.model.PopularSeriesViewModel
import com.dramabite.thebes.app.utils.coins.CoinsManager
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheet.CustomerConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.PaymentSheetResultCallback
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
class PremiumMemberActivity : BaseOtherActivity() {
    lateinit var binding: ActivityPremiumMemberBinding
    lateinit var popularSeriesViewModel: PopularSeriesViewModel
    private val http = OkHttpClient()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var isPaymentProcessing = false

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerConfig: CustomerConfiguration
    private lateinit var paymentIntentClientSecret: String

    private var pendingPurchaseType = 0
    private var pendingCoinsToAdd = 0L
    private var pendingPrice = 0.0
    private var selectedPlan = MembershipPlan.WEEKLY
    private var weeklyPrice = 0.0
    private var monthlyPrice = 0.0
    private var yearlyPrice = 0.0
    private var adShowing = false

    private enum class MembershipPlan {
        WEEKLY, MONTHLY, YEARLY
    }

    private lateinit var adapter: ShortDramaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPremiumMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textViewWeekMemberShip2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)
        binding.textViewMonthMemberShip2.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG)

        paymentSheet = PaymentSheet(
            this,
            PaymentSheetResultCallback { result: PaymentSheetResult ->
                this.onPaymentSheetResult(
                    result
                )
            })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.btnBack.setOnClickListener {
            finish()
        }

        setupMembershipPlans()
        setupPlanSwipe()

        binding.linearWeeklyMember.setOnClickListener {
            selectPlan(MembershipPlan.WEEKLY)
        }
        binding.linearMonthlyMember.setOnClickListener {
            selectPlan(MembershipPlan.MONTHLY)
        }
        binding.linearAnnualMember.setOnClickListener {
            selectPlan(MembershipPlan.YEARLY)
        }

        binding.buttonJoinNow.setOnClickListener {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser == null) {
                Toast.makeText(
                    this@PremiumMemberActivity,
                    getString(R.string.please_login_first),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (isPaymentProcessing) return@setOnClickListener

            val purchaseType: Int
            val price: Double
            when (selectedPlan) {
                MembershipPlan.WEEKLY -> {
                    purchaseType = RewardAdManager.PURCHASE_TYPE_WEEKLY
                    price = weeklyPrice
                }
                MembershipPlan.MONTHLY -> {
                    purchaseType = RewardAdManager.PURCHASE_TYPE_MONTHLY
                    price = monthlyPrice
                }
                MembershipPlan.YEARLY -> {
                    purchaseType = RewardAdManager.PURCHASE_TYPE_YEARLY
                    price = yearlyPrice
                }
            }
            if (price <= 0.0) return@setOnClickListener

            isPaymentProcessing = true
            startStripePayment(
                this@PremiumMemberActivity,
                price.toString().toFloat(),
                purchaseType,
                0L,
                price
            )
        }

        setupObserver()
    }


    private fun setupObserver() {

        popularSeriesViewModel = ViewModelProvider(this)
            .get(PopularSeriesViewModel::class.java)

        popularSeriesViewModel.getPopularSeries()
            .observe(this) { response ->

                if (response != null &&
                    response.data != null &&
                    response.data.isNotEmpty()
                ) {

                    binding.recycleView.layoutManager =
                        GridLayoutManager(this, 3)

                    binding.recycleView.setHasFixedSize(true)

                    adapter = ShortDramaAdapter(
                        this,
                        response.data,
                        0
                    )

                    binding.recycleView.adapter = adapter

                } else {
                }
            }
    }

    private fun setupMembershipPlans() {
        try {
            val country = ControlPreference.getCountryName()
            val jsonObject = if (country.equals("IN", ignoreCase = true)) {
                JSONObject(ControlPreference.getInrPlans())
            } else {
                JSONObject(ControlPreference.getUsdPlans())
            }

            val weeklyPlan = jsonObject.optJSONObject("weekly_membership")
            if (weeklyPlan != null) {
                setMemberPlan(
                    weeklyPlan,
                    binding.textViewWeekMemberShip1,
                    binding.textViewWeekMemberShip2,
                    binding.textViewWeekMemberShip3,
                    country
                )
                weeklyPrice = weeklyPlan.optDouble("main_price", 0.0)
                binding.linearWeeklyMember.visibility = View.VISIBLE
            } else {
                binding.linearWeeklyMember.visibility = View.GONE
            }

            val monthlyPlan = jsonObject.optJSONObject("monthly_membership")
            if (monthlyPlan != null) {
                setMemberPlan(
                    monthlyPlan,
                    binding.textViewMonthMemberShip1,
                    binding.textViewMonthMemberShip2,
                    binding.textViewMonthMemberShip3,
                    country
                )
                monthlyPrice = monthlyPlan.optDouble("main_price", 0.0)
                binding.linearMonthlyMember.visibility = View.VISIBLE
            } else {
                binding.linearMonthlyMember.visibility = View.GONE
            }

            val yearlyPlan = jsonObject.optJSONObject("yearly_membership")
            if (yearlyPlan != null) {
                setMemberPlan(
                    yearlyPlan,
                    binding.textViewYearMemberShip1,
                    binding.textViewYearMemberShip2,
                    binding.textViewYearMemberShip3,
                    country
                )
                yearlyPrice = yearlyPlan.optDouble("main_price", 0.0)
                binding.linearAnnualMember.visibility = View.VISIBLE
            } else {
                binding.linearAnnualMember.visibility = View.GONE
            }

            when {
                binding.linearWeeklyMember.visibility == View.VISIBLE -> selectPlan(MembershipPlan.WEEKLY)
                binding.linearMonthlyMember.visibility == View.VISIBLE -> selectPlan(MembershipPlan.MONTHLY)
                else -> selectPlan(MembershipPlan.YEARLY)
            }

            binding.root.post { sizePlanCardsForSwipe() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sizePlanCardsForSwipe() {
        val screenWidth = resources.displayMetrics.widthPixels
        val cardWidth = (screenWidth * 0.46f).toInt()
        listOf(
            binding.linearWeeklyMember,
            binding.linearMonthlyMember,
            binding.linearAnnualMember
        ).forEach { card ->
            if (card.visibility == View.VISIBLE) {
                val params = card.layoutParams
                params.width = cardWidth
                card.layoutParams = params
            }
        }
    }

    private fun setupPlanSwipe() {
        binding.scrollMembershipPlans.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                    view.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun selectPlan(plan: MembershipPlan) {
        selectedPlan = plan
        val padding = (26 * resources.displayMetrics.density).toInt()

        applyPlanSelection(binding.relativeWeeklyMemberShip, plan == MembershipPlan.WEEKLY, padding)
        applyPlanSelection(binding.relativeMonthlyMemberShip, plan == MembershipPlan.MONTHLY, padding)
        applyPlanSelection(binding.relativeAnnualMemberShip, plan == MembershipPlan.YEARLY, padding)
    }

    private fun applyPlanSelection(target: View, selected: Boolean, padding: Int) {
        target.setBackgroundResource(
            if (selected) R.drawable.background_reward_coins_rec3
            else R.drawable.background_annual_membership
        )
        target.setPadding(target.paddingLeft, padding, target.paddingRight, padding)
    }

    private fun setMemberPlan(
        plan5: JSONObject,
        tvWeeklyMemberShip1: TextView,
        tvWeeklyMemberShip2: TextView,
        tvWeeklyMemberTitle: TextView,
        country: String
    ) {
        try {
            val main_price = plan5.getDouble("main_price")
            val discount_price = plan5.getString("discount_price")
            val title = plan5.getString("title")
            tvWeeklyMemberTitle.maxLines = 1
            tvWeeklyMemberTitle.isSingleLine = true
            tvWeeklyMemberTitle.ellipsize = TextUtils.TruncateAt.END



            if (country.equals("IN", ignoreCase = true)) {
                tvWeeklyMemberShip1.setText("₹ " + main_price)
                tvWeeklyMemberShip2.setText(discount_price)
                tvWeeklyMemberTitle.setText(title)
            } else {
                tvWeeklyMemberShip1.setText("$ " + main_price)
                tvWeeklyMemberTitle.setText(discount_price)
                tvWeeklyMemberTitle.setText(" + " + title)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun startStripePayment(
        activity: Activity,
        amount: Float,
        purchaseType: Int,
        coinsToAdd: Long,
        priceForPremium: Double,

        ) {
        if (paymentSheet == null) {
            Toast.makeText(
                this@PremiumMemberActivity,
                "Stripe not initialized yet",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        pendingPurchaseType = purchaseType
        pendingCoinsToAdd = coinsToAdd
        pendingPrice = priceForPremium

        validateFromServerAndPresent(activity, amount)
    }

    private fun validateFromServerAndPresent(activity: Activity, amount: Float) {
        val apiUrl: String = ControlPreference.getTransactionUrl() +this@PremiumMemberActivity.getString(R.string.end_point)
        val country = ControlPreference.getCountryName()
        val currency =
            if (country != null && country.equals("IN", ignoreCase = true)) "inr" else "usd"

        val user = FirebaseAuth.getInstance().getCurrentUser()
        val email =
            if (user != null && user.getEmail() != null) user.getEmail() else "user@example.com"
        val name = if (user != null && user.getDisplayName() != null)
            user.getDisplayName()
        else
            (if (email!!.contains("@")) email.substring(0, email.indexOf('@')) else "User")

        val amountSmallestUnit = (amount * 100f).toLong()

        val userId = FirebaseAuth.getInstance().uid
        val body = FormBody.Builder()
            .add("amount", amountSmallestUnit.toString())
            .add("currency", currency)
            .add("email", email!!)
            .add("name", name!!)
            .add("user_id", userId ?: "")
            .build()

        val request = Request.Builder()
            .url(apiUrl)
            .post(body)
            .build()

        http.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                Log.e("StripePayment", "Network failure: ${e.message}", e)
                isPaymentProcessing = false
                mainHandler.post(Runnable {
                    Toast.makeText(
                        this@PremiumMemberActivity,
                        getString(R.string.payment_init_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                })
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    response.use { r ->
                        if (!r.isSuccessful) {
//                            Log.e("StripePayment", "Server returned error: ${r.code} ${r.message}")
                            isPaymentProcessing = false
                            mainHandler.post(Runnable {
                                Toast.makeText(
                                    this@PremiumMemberActivity,
                                    getString(R.string.payment_init_failed_server) +" ${r.code}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                            return
                        }
                        val json = if (r.body != null) r.body!!.string() else ""
                        val responseJson = JSONObject(json)
                        paymentIntentClientSecret = responseJson.getString("paymentIntent")
                        val customerId = responseJson.getString("customer")
                        val ephemeralKeySecret = responseJson.getString("ephemeralKey")
                        val publishableKey = responseJson.getString("publishableKey")
                        customerConfig = CustomerConfiguration(customerId, ephemeralKeySecret)
                        mainHandler.post(Runnable {
                            try {
                                PaymentConfiguration.init(
                                    activity.getApplicationContext(),
                                    publishableKey
                                )
                                val configuration =
                                    PaymentSheet.Configuration.Builder("DramaBite")
                                        .customer(customerConfig)
                                        .allowsDelayedPaymentMethods(true)
                                        .build()
                                paymentSheet.presentWithPaymentIntent(
                                    paymentIntentClientSecret,
                                    configuration
                                )
                            } catch (e: java.lang.Exception) {
//                                Log.e("StripePayment", "Error presenting PaymentSheet: ${e.message}", e)
                                isPaymentProcessing = false
                                Toast.makeText(
                                    this@PremiumMemberActivity,
                                    "Stripe error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                } catch (e: java.lang.Exception) {
//                    Log.e("StripePayment", "Error parsing response or presenting: ${e.message}", e)
                    isPaymentProcessing = false
                    mainHandler.post(Runnable {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.payment_init_failed_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }
            }
        })
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        if (paymentSheetResult is PaymentSheetResult.Completed) {
            isPaymentProcessing = false

            onStripePaymentCompleted()
        } else if (paymentSheetResult is PaymentSheetResult.Canceled) {
            isPaymentProcessing = false

            Toast.makeText(this@PremiumMemberActivity, getString(R.string.payment_cancelled), Toast.LENGTH_SHORT)
                .show()
        } else if (paymentSheetResult is PaymentSheetResult.Failed) {
            isPaymentProcessing = false

            val failed = paymentSheetResult
            Toast.makeText(
                this@PremiumMemberActivity,
                getString(R.string.payment_failed) + ": " + (if (failed.error != null) failed.error.message else ""),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun onStripePaymentCompleted() {
        val uid = FirebaseAuth.getInstance().getUid()
        if (uid == null) return

        if (pendingPurchaseType >= RewardAdManager.PURCHASE_TYPE_COINS_PACK_1 && pendingPurchaseType <= RewardAdManager.PURCHASE_TYPE_COINS_PACK_4) {
            val coinsToAddNow: Long = pendingCoinsToAdd
            if (coinsToAddNow > 0) {
                CoinsManager.addCoins(
                    uid, coinsToAddNow,
                    OnSuccessListener { unused: Void? ->
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            "+" + coinsToAddNow + " " + getString(R.string.coins_added),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    OnFailureListener { e: java.lang.Exception? ->
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.failed_to_add_coin),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
            }
        } else if (pendingPurchaseType == RewardAdManager.PURCHASE_TYPE_WEEKLY) {
            PremiumPlanManager.savePremiumPurchaseToFirestore(
                this@PremiumMemberActivity, true, false, pendingPrice,
                PremiumPlanManager.DURATION_WEEKLY_MS, object : SavePremiumCallback {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.weekly_membership_activied),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.failed_to_activate_membership),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else if (pendingPurchaseType == RewardAdManager.PURCHASE_TYPE_MONTHLY) {
            PremiumPlanManager.savePremiumPurchaseToFirestore(
                this@PremiumMemberActivity, false, false, pendingPrice,
                PremiumPlanManager.DURATION_MONTHLY_MS, object : SavePremiumCallback {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.monthly_membership_activied),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.failed_to_activate_membership),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else if (pendingPurchaseType == RewardAdManager.PURCHASE_TYPE_YEARLY) {
            PremiumPlanManager.savePremiumPurchaseToFirestore(
                this@PremiumMemberActivity, false, true, pendingPrice,
                PremiumPlanManager.DURATION_YEARLY_MS, object : SavePremiumCallback {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.yearly_membership_activied),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Toast.makeText(
                            this@PremiumMemberActivity,
                            getString(R.string.failed_to_activate_membership),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        adShowing = false
        notifyLockUiHidden()



        pendingPurchaseType = 0
        pendingCoinsToAdd = 0L
        pendingPrice = 0.0
    }

//    private var lockUiListener: LockUiListener? = null

    private fun notifyLockUiHidden() {
//        if (lockUiListener != null) lockUiListener.onLockUiHidden()
//        for (r in RewardAdManager.onLockUiHiddenRunnables) {
//            try {
//                r.run()
//            } catch (ignored: java.lang.Exception) {
//            }
//        }
    }
}
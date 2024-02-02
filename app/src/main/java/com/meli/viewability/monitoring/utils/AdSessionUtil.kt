package com.meli.viewability.monitoring.utils

import android.content.Context
import android.util.Log
import android.view.View
import com.google.firebase.perf.metrics.AddTrace
import com.iab.omid.library.mercadolibre.Omid
import com.iab.omid.library.mercadolibre.adsession.AdSession
import com.iab.omid.library.mercadolibre.adsession.AdSessionConfiguration
import com.iab.omid.library.mercadolibre.adsession.AdSessionContext
import com.iab.omid.library.mercadolibre.adsession.CreativeType
import com.iab.omid.library.mercadolibre.adsession.ImpressionType
import com.iab.omid.library.mercadolibre.adsession.Owner
import com.iab.omid.library.mercadolibre.adsession.Partner
import com.iab.omid.library.mercadolibre.adsession.VerificationScriptResource
import com.meli.viewability.monitoring.BuildConfig
import com.meli.viewability.monitoring.R
import com.meli.viewability.monitoring.ui.adapters.ComponentsAdapter
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import kotlin.system.measureTimeMillis

@Throws(MalformedURLException::class)
@AddTrace(name = "getNativeAdSession")
fun getNativeAdSession(
    context: Context?,
    customReferenceData: String?,
    creativeType: CreativeType,
    logContext: String? = ""
): AdSession {

    val timeEnsureOmidActivated = measureTimeMillis {
        ensureOmidActivated(context)
    }

    val adSessionConfiguration: AdSessionConfiguration
    val timeCreateAdSessionConfiguration = measureTimeMillis {
        adSessionConfiguration = AdSessionConfiguration.createAdSessionConfiguration(
            creativeType,
            ImpressionType.VIEWABLE,
            Owner.NATIVE,
            Owner.NONE,
            false
        )
    }

    val partner: Partner
    val timeCreatePartner = measureTimeMillis {
        partner = Partner.createPartner(BuildConfig.PARTNER_NAME, BuildConfig.VERSION_NAME)
    }

    val omidJs: String
    val timeGetOmidJs = measureTimeMillis {
        omidJs = getOmidJs(context)
    }

    val verificationScripts: List<VerificationScriptResource>
    val timeGetVerificationScriptResources = measureTimeMillis {
        verificationScripts = getVerificationScriptResources()
    }


    val adSessionContext: AdSessionContext
    val timeCreateNativeAdSessionContext = measureTimeMillis {
        adSessionContext = AdSessionContext.createNativeAdSessionContext(
            partner,
            omidJs,
            verificationScripts,
            null,
            customReferenceData
        )
    }

    val adSession: AdSession
    val timeCreateAdSession = measureTimeMillis {
        adSession = AdSession.createAdSession(adSessionConfiguration, adSessionContext)
    }

    Log.d(TAG, "---- INIT METRICS ---- : $logContext".trim())
    Log.d(TAG, "$logContext timeEnsureOmidActivated : ${timeEnsureOmidActivated}ms".trim())
    Log.d(TAG, "$logContext timeCreateAdSessionConfiguration : ${timeCreateAdSessionConfiguration}ms".trim())
    Log.d(TAG, "$logContext timeCreatePartner : ${timeCreatePartner}ms".trim())
    Log.d(TAG, "$logContext timeGetOmidJs : ${timeGetOmidJs}ms".trim())
    Log.d(TAG, "$logContext timeGetVerificationScriptResources : ${timeGetVerificationScriptResources}ms".trim())
    Log.d(TAG, "$logContext timeCreateNativeAdSessionContext : ${timeCreateNativeAdSessionContext}ms".trim())
    Log.d(TAG, "$logContext timeCreateAdSession : ${timeCreateAdSession}ms".trim())

    return adSession
}

@AddTrace(name = "ensureOmidActivated")
private fun ensureOmidActivated(context: Context?) {
    Omid.activate(context?.applicationContext)
}

@Throws(MalformedURLException::class)
@AddTrace(name = "getVerificationScriptResources")
private fun getVerificationScriptResources(): List<VerificationScriptResource> {
    return listOf(
        VerificationScriptResource.createVerificationScriptResourceWithParameters(
            BuildConfig.VENDOR_KEY,
            getURL(),
            BuildConfig.VERIFICATION_PARAMETERS
        )
    )
}

@Throws(MalformedURLException::class)
@AddTrace(name = "getURL")
private fun getURL(): URL {
    return URL(BuildConfig.VERIFICATION_URL)
}

fun getOmidJs(context: Context?): String {
    val res = context?.resources
    res?.let {
        try {

                res.openRawResource(R.raw.omsdk_v1).use { inputStream ->
                    val b = ByteArray(inputStream.available())
                    val bytesRead = inputStream.read(b)
                    return String(b, 0, bytesRead, charset("UTF-8"))
                }

        } catch (e: IOException) {
            throw UnsupportedOperationException("Yikes, omid resource not found", e)
        }
    } ?: run {
        return ""
    }
}

fun optimizeInitializationOmsdk(context: Context?) {
    Log.d(TAG, "*** START INITIAL_OPTIMIZATION ***")

    var adSession: AdSession? = null

    val timeCreateNativeSession = measureTimeMillis {
        try {
            adSession = getNativeAdSession(
                context,
                ComponentsAdapter.CUSTOM_REFERENCE_DATA,
                CreativeType.NATIVE_DISPLAY,
                "INITIAL_OPTIMIZATION"
            )
        } catch (e: MalformedURLException) {
            Log.d(TAG, "setupAdSession failed", e)
        }
    }

    adSession?.let { session ->
        val timeRegisterAdView = measureTimeMillis {
            session.registerAdView(View(context))
        }

        val timeSessionStart = measureTimeMillis {
            session.start()
        }

        Log.d(TAG, "DASH INITIAL_OPTIMIZATION -> timeRegisterAdView : ${timeRegisterAdView}ms")
        Log.d(TAG, "DASH INITIAL_OPTIMIZATION -> timeSessionStart : ${timeSessionStart}ms")
    }

    Log.d(TAG, "DASH INITIAL_OPTIMIZATION -> timeCreateNativeSession : ${timeCreateNativeSession}ms")

    Log.d(TAG, "*** FINISH INITIAL_OPTIMIZATION ***")
    adSession.let { session ->
        session?.finish()
        session?.let { adSession = null }
    }
}
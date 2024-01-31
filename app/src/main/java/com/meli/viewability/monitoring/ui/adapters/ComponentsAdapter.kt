package com.meli.viewability.monitoring.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.perf.metrics.AddTrace
import com.iab.omid.library.mercadolibre.adsession.AdEvents
import com.iab.omid.library.mercadolibre.adsession.AdSession
import com.iab.omid.library.mercadolibre.adsession.CreativeType
import com.iab.omid.library.mercadolibre.adsession.FriendlyObstructionPurpose
import com.meli.viewability.monitoring.R
import com.meli.viewability.monitoring.databinding.TemplateComponentBinding
import com.meli.viewability.monitoring.domain.models.Component
import com.meli.viewability.monitoring.domain.models.ComponentSize
import com.meli.viewability.monitoring.domain.models.ComponentType
import com.meli.viewability.monitoring.utils.TAG
import com.meli.viewability.monitoring.utils.getNativeAdSession
import java.net.MalformedURLException
import kotlin.system.measureTimeMillis

class ComponentsAdapter() : RecyclerView.Adapter<ComponentsAdapter.ViewHolder>()  {

    private val items : MutableList<Component> = mutableListOf()

    fun setData(items: List<Component>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TemplateComponentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = items[position]
        holder.bind(category)
    }

    class ViewHolder (
        private val binding: TemplateComponentBinding,
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(component: Component) = with(binding) {
            fixSize(binding, component.size, binding.root.context)
            when (component.type) {
                ComponentType.ADS -> {
                    tvName.visibility = View.GONE
                    setupAdView(binding.root.context, binding)
                    showAds(binding, component)

                }
                ComponentType.MOCK -> {
                    ivAds.visibility = View.GONE
                    tvBoxAds.visibility = View.GONE
                    tvName.visibility = View.VISIBLE
                }

                else -> {

                }
            }
        }

        private fun fixSize(binding: TemplateComponentBinding, size : ComponentSize?, context : Context){
            when(size){
                ComponentSize.SMALL -> {
                    binding.card.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.small)
                }

                ComponentSize.MEDIUM -> {
                    binding.card.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.medium)
                }

                ComponentSize.LARGE -> {
                    binding.card.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.large)
                }

                else -> {
                    binding.card.layoutParams.height = context.resources.getDimensionPixelSize(R.dimen.small)
                }
            }
        }

        private fun showAds(binding: TemplateComponentBinding, component: Component) = with(binding) {
            ivAds.visibility = View.VISIBLE
            tvBoxAds.visibility = View.VISIBLE

            if(component.businessData.isNotEmpty()){
                component.businessData["picture"]?.takeIf { it.isNotEmpty() }?.apply {
                    Glide.with(root)
                        .load(this)
                        .into(ivAds)
                }
            }
        }

        @AddTrace(name = "setupAdView")
        private fun setupAdView(context: Context, binding: TemplateComponentBinding) {
            var adSession : AdSession? = null

            val timeCreateNativeSession = measureTimeMillis {
                try {
                    adSession = getNativeAdSession(
                        context,
                        CUSTOM_REFERENCE_DATA,
                        CreativeType.NATIVE_DISPLAY
                    )
                } catch (e: MalformedURLException) {
                    Log.d(TAG, "setupAdSession failed", e)
                }
            }

            Log.d(TAG, "timeCreateNativeSession : ${timeCreateNativeSession}ms")

            adSession?.let { session ->
                val timeRegisterAdView = measureTimeMillis {
                    session.registerAdView(binding.ivAds)
                }

                val timeAddFriendlyObstruction = measureTimeMillis {
                    session.addFriendlyObstruction(
                        binding.tvBoxAds,
                        FriendlyObstructionPurpose.OTHER,
                        null
                    )
                }

                val timeSessionStart = measureTimeMillis {
                    session.start()
                }

                val adEvents : AdEvents
                val timeCreateAdEvents = measureTimeMillis {
                    adEvents = AdEvents.createAdEvents(session)
                }

                val timeLoaded = measureTimeMillis {
                    adEvents.loaded()
                }

                val timeImpressionOccurred = measureTimeMillis {
                    adEvents.impressionOccurred()
                }

                Log.d(TAG, "timeRegisterAdView : ${timeRegisterAdView}ms")
                Log.d(TAG, "timeAddFriendlyObstruction : ${timeAddFriendlyObstruction}ms")
                Log.d(TAG, "timeSessionStart : ${timeSessionStart}ms")
                Log.d(TAG, "timeCreateAdEvents : ${timeCreateAdEvents}ms")
                Log.d(TAG, "timeLoaded : ${timeLoaded}ms")
                Log.d(TAG, "timeImpressionOccurred : ${timeImpressionOccurred}ms")
                Log.d(TAG, "---- FINISH METRICS ----")

            }
        }
    }

    companion object {
        const val CUSTOM_REFERENCE_DATA = "{ \"birthday\":-310957844000, \"user\":\"me\" }"
    }
}
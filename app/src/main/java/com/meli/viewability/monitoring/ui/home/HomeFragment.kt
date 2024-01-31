package com.meli.viewability.monitoring.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.meli.viewability.monitoring.databinding.FragmentHomeBinding
import com.meli.viewability.monitoring.domain.models.Component
import com.meli.viewability.monitoring.ui.adapters.ComponentsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : ComponentsAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.getComponents()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        addObservers()
        viewModel.getComponents()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipe.setOnRefreshListener {
            viewModel.getComponents()
        }

        adapter = ComponentsAdapter()

        with(binding.rvComponents){
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@HomeFragment.adapter
        }
    }

    private fun addObservers() {
        viewModel.stateLiveData.observe(viewLifecycleOwner) { handleViewState(it) }
    }

    private fun handleViewState(viewState: HomeViewModel.ViewState) {
        when(viewState) {
            HomeViewModel.ViewState.LoadingComponents -> startLoading()
            is HomeViewModel.ViewState.LoadedComponents -> onLoadedQuick(viewState.items)
            HomeViewModel.ViewState.ErrorLoadComponents -> onErrorGettingComponents()
            HomeViewModel.ViewState.EmptyComponents-> onEmptyComponents()
            else -> {
                onErrorGettingComponents()
            }
        }
    }

    private fun startLoading() = with(binding) {
        progressBar.visibility = View.VISIBLE
    }

    private fun stopLoading() = with(binding) {
        progressBar.visibility = View.GONE
        swipe.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onLoadedQuick(items: List<Component>) {
        stopLoading()
        adapter.setData(items)
    }

    private fun onEmptyComponents() {
        Snackbar.make(binding.rvComponents, "No hay componentes", Snackbar.LENGTH_LONG).show()
        stopLoading()
    }

    private fun onErrorGettingComponents() {
        Snackbar.make(binding.rvComponents, "Error cargando componentes", Snackbar.LENGTH_LONG).show()
        stopLoading()
    }
}
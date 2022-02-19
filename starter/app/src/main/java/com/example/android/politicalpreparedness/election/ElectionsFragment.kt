package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    lateinit var viewModel: ElectionsViewModel
    lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentElectionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val database = ElectionDatabase.getInstance(requireActivity())
        val app = requireNotNull(this.activity).application
        val factory = ElectionsViewModelFactory(app, database)
        viewModel = ViewModelProvider(this, factory).get(ElectionsViewModel::class.java)

        binding.viewModel = viewModel

        val electionListAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    it.id,
                    it.division
                )
            )
        })

        val savedListAdapter = ElectionListAdapter(ElectionListener {
            findNavController().navigate(
                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    it.id,
                    it.division
                )
            )
        })

        binding.upcomingElectionsRecyclerview.adapter = electionListAdapter
        binding.savedElectionsRecyclerview.adapter = savedListAdapter

        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                //update recycler adapter
                electionListAdapter.submitList(it.elections)
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                //update recycler adapter
                savedListAdapter.submitList(it)
            }
        })

        viewModel.showLoading.observe(viewLifecycleOwner, {
            it?.let {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.refreshElections()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onClear()
    }
}
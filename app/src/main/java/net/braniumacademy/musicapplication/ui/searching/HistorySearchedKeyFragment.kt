package net.braniumacademy.musicapplication.ui.searching

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import net.braniumacademy.musicapplication.R
import net.braniumacademy.musicapplication.data.model.searching.HistorySearchedKey
import net.braniumacademy.musicapplication.databinding.FragmentHistorySearchedKeyBinding
import net.braniumacademy.musicapplication.databinding.ItemSearchedKeyBinding
import net.braniumacademy.musicapplication.ui.dialog.ConfirmationDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class HistorySearchedKeyFragment : Fragment() {
    private lateinit var binding: FragmentHistorySearchedKeyBinding
    private lateinit var adapter: HistorySearchedKeyAdapter

    @Inject
    lateinit var searchingViewModelFactory: SearchingViewModel.Factory
    private val viewModel: SearchingViewModel by activityViewModels {
        searchingViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistorySearchedKeyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserver()
    }

    private fun setupView() {
        adapter = HistorySearchedKeyAdapter(object : OnItemClickListener {
            override fun onClick(key: String) {
                viewModel.setSelectedKey(key)
            }
        })
        binding.rvHistoryKey.adapter = adapter
        binding.tvClearHistorySearchedKey.setOnClickListener {
            val message = R.string.message_confirm_clear_history
            val dialog = ConfirmationDialogFragment(
                message,
                object : ConfirmationDialogFragment.OnDeleteConfirmListener {
                    override fun onConfirm(isConfirmed: Boolean) {
                        if (isConfirmed) {
                            viewModel.clearAllKeys()
                        }
                    }
                })
            dialog.show(requireActivity().supportFragmentManager, ConfirmationDialogFragment.TAG)
        }
    }

    private fun setupObserver() {
        viewModel.keys.observe(viewLifecycleOwner) { keys ->
            adapter.updateKeys(keys)
        }
    }

    internal class HistorySearchedKeyAdapter(
        private val listener: OnItemClickListener
    ) : RecyclerView.Adapter<HistorySearchedKeyAdapter.ViewHolder>() {
        private val keys = mutableListOf<HistorySearchedKey>()

        internal class ViewHolder(
            private val binding: ItemSearchedKeyBinding,
            private val listener: OnItemClickListener
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(key: HistorySearchedKey) {
                binding.tvSearchedKey.text = key.key
                binding.root.setOnClickListener {
                    listener.onClick(key.key)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemSearchedKeyBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding, listener)
        }

        override fun getItemCount(): Int {
            return keys.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(keys[position])
        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateKeys(newKeys: List<HistorySearchedKey>) {
            keys.clear()
            keys.addAll(newKeys)
            notifyDataSetChanged()
        }
    }

    internal interface OnItemClickListener {
        fun onClick(key: String)
    }
}
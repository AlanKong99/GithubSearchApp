package alan_kong99.com.github.android.githubsearchapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.githubsearchapp.databinding.FragmentSearchBinding
import alan_kong99.com.github.android.githubsearchapp.viewmodel.GithubSearchViewModel
import android.util.Log
import android.widget.Toast
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.bignerdranch.android.githubsearchapp.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class SearchFragment : Fragment() {

    private lateinit var viewModel: GithubSearchViewModel
    private lateinit var  binding: FragmentSearchBinding
    private var searchJob: Job? = null
    private lateinit var repoAdapter: RepoAdapter
    private lateinit var usrAdapter: UserAdapter
    private lateinit var searchChoices: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        searchChoices = resources.getStringArray(R.array.search_choices)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(GithubSearchViewModel::class.java)

        binding.searchContent.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.searchText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.setLastSearchChoice(binding.searchChoice.selectedItemId)
                decideAdapter(binding.searchChoice.selectedItem.toString())
                search(binding.searchChoice.selectedItem.toString())
                true
            } else {
                false
            }
        }

        binding.searchButton.setOnClickListener {
            viewModel.setLastSearchChoice(binding.searchChoice.selectedItemId)
            decideAdapter(binding.searchChoice.selectedItem.toString())
            search(binding.searchChoice.selectedItem.toString())
        }

        initAdapter()

        binding.searchChoice.setSelection(viewModel.getLastSearchChoice())

        return binding.root
    }

    private fun initAdapter() {
        repoAdapter = RepoAdapter()
        repoAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { repoAdapter.retry() },
            footer = LoadStateAdapter { repoAdapter.retry() }
        )
        repoAdapter.addLoadStateListener {loadState->
            reactionToLoadState(loadState)
        }

        usrAdapter = UserAdapter(requireContext())
        usrAdapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { usrAdapter.retry() },
            footer = LoadStateAdapter { usrAdapter.retry() }
        )
        usrAdapter.addLoadStateListener {loadState->
            reactionToLoadState(loadState)
        }
    }

    private fun reactionToLoadState(loadState: CombinedLoadStates) {
        changeVisibility(binding.loadProgressBar, loadState.source.refresh is LoadState.Loading)
        changeVisibility(binding.searchContent, loadState.source.refresh is LoadState.NotLoading)
        changeVisibility(binding.retryButton, loadState.source.refresh is LoadState.Error)
        val errorState = loadState.source.append as? LoadState.Error
            ?: loadState.source.prepend as? LoadState.Error
            ?: loadState.append as? LoadState.Error
            ?: loadState.prepend as? LoadState.Error
        errorState?.let {
            Toast.makeText(
                requireContext(),
                "\uD83D\uDE28 Wooops ${it.error}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun changeVisibility(view: View, boolean: Boolean) {
        if (boolean) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchText.text.append(viewModel.getLastQuery())

        binding.retryButton.setOnClickListener {
            when (binding.searchChoice.selectedItem.toString()) {
                searchChoices[0] -> repoAdapter.retry()
                searchChoices[1] -> usrAdapter.retry()
            }
        }

        decideAdapter(binding.searchChoice.selectedItem.toString())
        search(binding.searchChoice.selectedItem.toString())
    }

    private fun decideAdapter(choice: String) {
        when (choice) {
            searchChoices[0] -> binding.searchContent.adapter = repoAdapter
            searchChoices[1] -> binding.searchContent.adapter = usrAdapter
        }
    }

    private fun search(choice: String) {
        when (choice) {
            searchChoices[0] -> beforeUpdateRepoFromInput()
            searchChoices[1] -> beforeUpdateUsrFromInput()
        }
    }

    private fun beforeUpdateRepoFromInput() {
        binding.searchText.text.trim().let{
            Log.i("TAG", "beforeUpdateRepoFromInput: $it")
            if (it.isNotBlank()) {
                binding.searchContent.scrollToPosition(0)
                viewModel.setRepoQuery(it.toString())
                binding.searchText.text.clear()
                searchRepo()
            }
        }
    }

    private fun searchRepo() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getRepos().collectLatest {
                repoAdapter.submitData(it)
            }
        }
    }

    private fun beforeUpdateUsrFromInput(){
        binding.searchText.text.trim().let {
            if (it.isNotBlank()) {
                binding.searchContent.scrollToPosition(0)
                viewModel.setUsrQuery(it.toString())
                binding.searchText.text.clear()
                searchUser()
            }
        }
    }

    private fun searchUser(){
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.getUsers().collectLatest {
                usrAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        usrAdapter.thumbnailLoaders.forEach {
            it.clearQueue()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        usrAdapter.thumbnailLoaders.forEach {
            it.quit()
        }
    }
}
package com.Nihilisttt.LearnWord.Fragment.SearchPage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.Nihilisttt.LearnWord.Adapter.SearchResultAdapter;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageStateViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.LearnPageViewModel;
import com.Nihilisttt.LearnWord.Page.ViewModel.SearchPageViewModel;
import com.Nihilisttt.LearnWord.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SearchPageFragment extends Fragment {
    private EditText searchInput;
    private ImageButton clearBtn;
    private TextView cancelBtn;
    private FrameLayout contentContainer;
    private RecyclerView resultsList;
    private FloatingActionButton backFab;
    private SearchResultAdapter adapter;
    private SearchPageViewModel searchViewModel;
    private LearnPageViewModel learnViewModel;
    private LearnPageStateViewModel stateViewModel;
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable debounceRunnable;
    private static final long DEBOUNCE_DELAY = 300;
    private boolean isInDetailPage = false;

    private final TextWatcher searchTextWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {
            String query = s.toString();
            clearBtn.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
            debounceRunnable = () -> searchViewModel.search(query);
            debounceHandler.postDelayed(debounceRunnable, DEBOUNCE_DELAY);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(SearchPageViewModel.class);
        learnViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(LearnPageStateViewModel.class);

        searchInput = view.findViewById(R.id.search_input);
        clearBtn = view.findViewById(R.id.search_clear_btn);
        cancelBtn = view.findViewById(R.id.search_cancel_btn);
        contentContainer = view.findViewById(R.id.search_content_container);
        backFab = view.findViewById(R.id.search_detail_back_fab);

        resultsList = new RecyclerView(requireContext());
        resultsList.setId(View.generateViewId());
        resultsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SearchResultAdapter(result -> showDetailPage(result.wordId));
        resultsList.setAdapter(adapter);
        resultsList.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        contentContainer.addView(resultsList);

        setupSearchBar();
        setupObservers();
        setupBackFab();
    }

    private void setupSearchBar() {
        searchInput.addTextChangedListener(searchTextWatcher);

        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isInDetailPage) {
                showMultiPage();
            }
        });

        searchInput.setOnClickListener(v -> {
            if (isInDetailPage) {
                showMultiPage();
            }
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
                searchViewModel.search(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        clearBtn.setOnClickListener(v -> {
            searchInput.setText("");
            searchInput.requestFocus();
            showKeyboard();
        });

        cancelBtn.setOnClickListener(v -> navigateBackToMain());
    }

    private void setupObservers() {
        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), results -> {
            if (!isInDetailPage) {
                adapter.setResults(results);
            }
        });
    }

    private void setupBackFab() {
        backFab.setOnClickListener(v -> navigateBackToMain());
    }

    private void fillCurrentWord() {
        LearnPageViewModel.CombinedWordInfo info = learnViewModel.getCombinedWordInfo().getValue();
        if (info != null) {
            String kanji = info.getBasicWord().getCompositeKanji();
            if (kanji != null && !kanji.isEmpty()) {
                searchInput.removeTextChangedListener(searchTextWatcher);
                searchInput.setText(kanji);
                searchInput.setSelection(kanji.length());
                searchInput.addTextChangedListener(searchTextWatcher);
                clearBtn.setVisibility(View.VISIBLE);
                searchViewModel.search(kanji);
            }
        }
    }

    private void showDetailPage(String wordId) {
        hideKeyboard();
        searchInput.clearFocus();
        isInDetailPage = true;
        resultsList.setVisibility(View.GONE);
        backFab.setVisibility(View.VISIBLE);

        SearchDetailFragment detailFragment = SearchDetailFragment.newInstance(wordId);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(contentContainer.getId(), detailFragment, "SearchDetail");
        ft.commit();
    }

    private void showMultiPage() {
        isInDetailPage = false;
        backFab.setVisibility(View.GONE);
        removeDetailFragment();
        resultsList.setVisibility(View.VISIBLE);
        adapter.setResults(searchViewModel.getSearchResults().getValue());
        searchInput.requestFocus();
        showKeyboard();
    }

    private void navigateBackToMain() {
        searchInput.setText("");
        hideKeyboard();
        if (isInDetailPage) {
            isInDetailPage = false;
            backFab.setVisibility(View.GONE);
            removeDetailFragment();
            resultsList.setVisibility(View.VISIBLE);
        }
        ViewPager2 outerVp2 = requireActivity().findViewById(R.id.learn_page_vp2_container);
        if (outerVp2 != null) {
            outerVp2.setCurrentItem(1, true);
        }
    }

    private void removeDetailFragment() {
        Fragment detailFragment = getChildFragmentManager().findFragmentByTag("SearchDetail");
        if (detailFragment != null) {
            getChildFragmentManager().beginTransaction().remove(detailFragment).commitNowAllowingStateLoss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisibleToUser()) {
            stateViewModel.setWhichFragmentInLearnPage(
                    LearnPageStateViewModel.FragmentInLearnPage.SearchFragment);
            if (!isInDetailPage) {
                resultsList.setVisibility(View.VISIBLE);
                fillCurrentWord();
                searchInput.requestFocus();
            }
        }
    }

    private boolean isVisibleToUser() {
        return getUserVisibleHint() || isVisible();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(searchInput, 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (debounceRunnable != null) debounceHandler.removeCallbacks(debounceRunnable);
    }
}

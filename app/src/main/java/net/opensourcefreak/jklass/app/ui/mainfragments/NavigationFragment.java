package net.opensourcefreak.jklass.app.ui.mainfragments;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import net.opensourcefreak.jklass.app.ui.homepage.MainActivity;

public class NavigationFragment extends Fragment {
    public NavigationFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    NavController getNavController() {
        return ((MainActivity) getActivity()).getNavController();
    }
}

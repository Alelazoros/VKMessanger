package ua.nure.vkmessanger.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import ua.nure.vkmessanger.R;
import ua.nure.vkmessanger.activity.FriendsActivity;

/**
 * Created by Antony on 5/29/2016.
 */
public class NavigationDrawer {

    public static final int ITEM_DIALOGS = 1;
    public static final int ITEM_FRIENDS = 2;
    public static final int ITEM_SETTINGS = 3;
    public static final int ITEM_HELP = 4;

    private Activity activity;
    private Toolbar toolbar;

    public NavigationDrawer(Activity activity, Toolbar toolbar) {
        this.activity = activity;
        this.toolbar = toolbar;
    }

    public Drawer getMaterialDrawer() {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header_background)
                .withTranslucentStatusBar(false)
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.navigation_item_dialogs)//.withIcon(R.drawable.ic_map)
                                .withIdentifier(ITEM_DIALOGS),
                        new PrimaryDrawerItem().withName(R.string.navigation_item_friends)//.withIcon(R.drawable.ic_magnify)
                                .withIdentifier(ITEM_FRIENDS),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.navigation_item_settings)//.withIcon(R.drawable.ic_settings)
                                .withIdentifier(ITEM_SETTINGS),
                        new PrimaryDrawerItem().withName(R.string.navigation_item_help_name)//.withIcon(R.drawable.ic_help)
                                .withIdentifier(ITEM_HELP)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case ITEM_DIALOGS:
                                break;
                            case ITEM_FRIENDS:
                                FriendsActivity.newIntent(activity, ITEM_FRIENDS, FriendsActivity.GET_FRIENDS);
                                break;
                            case ITEM_SETTINGS:
                                break;
                            case ITEM_HELP:
                                break;
                        }
                        return false;
                    }
                })
                .withSelectedItem(1)
                .build();

        //Анимация проворота иконки при клике на нее для вызова drawer-а.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                activity,
                result.getDrawerLayout(),
                toolbar,
                R.string.navigation_view_open,
                R.string.navigation_view_close);
        result.getDrawerLayout().setDrawerListener(toggle);
        toggle.syncState();

        return result;
    }
}
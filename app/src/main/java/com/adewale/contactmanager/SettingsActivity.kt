package com.adewale.contactmanager

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.adewale.contactmanager.viewmodels.ContactListViewModel
import com.adewale.contactmanager.viewmodels.ContactListViewModelFactory
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : AppCompatActivity() {

    //shared preferences
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        //Set the theme
        var themeIsChcked = false
        sharedPrefs = getSharedPreferences(BaseApp.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if(sharedPrefs.getInt(BaseApp.THEME_PREFERENCE, BaseApp.THEME_LIGHT) == BaseApp.THEME_DARK) {
            setTheme(R.style.DarkTheme)
        } else {
            themeIsChcked = true
            setTheme(R.style.LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = getString(R.string.title_activity_settings)

        settings_notification_switch.isChecked =
            sharedPrefs.getBoolean(BaseApp.NOTIFICATION_PREFERENCE, true)
        settings_theme_switch.isChecked = themeIsChcked

        if(sharedPrefs.getInt(BaseApp.THEME_PREFERENCE, BaseApp.THEME_LIGHT) == BaseApp.THEME_DARK) {
            settings_theme_switch.isChecked = true
            setTheme(R.style.DarkTheme)
        } else {
            settings_theme_switch.isChecked = false
            setTheme(R.style.LightTheme)
        }

        settings_delete_all_relative_layout.setOnClickListener(deleteAllOnClick)
        settings_birthday_notification_relative_layout.setOnClickListener(notificationOnClickListener)
        settings_notification_switch.setOnCheckedChangeListener(notificationSwitchOnCheckedListener)
        settings_theme_relative_layout.setOnClickListener(themeOnClickListener)
        settings_theme_switch.setOnCheckedChangeListener(themeSwitchOnCheckListener)
    }

    /**
     * View Listener for deleting all contacts
     */
    private val deleteAllOnClick = View.OnClickListener {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_delete_all_alert_title))
            .setMessage(getString(R.string.settings_delete_all_description))
            .setPositiveButton(getString(R.string.alert_delete)) {dialog, which ->
                val viewModel = ViewModelProviders.of(this,
                    ContactListViewModelFactory(this.application)
                ).get(ContactListViewModel::class.java)
                viewModel.deleteAllContacts()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_all_contacts_deleted),
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton(getString(R.string.alert_cancel),null)
        builder.show()
    }

    /**
     * View Listener to turn on/off the birthday notifications
     */
    private val notificationOnClickListener = View.OnClickListener {
        settings_notification_switch.isChecked = !(settings_notification_switch.isChecked)
    }

    /**
     * Switch Listener to turn on/off the birthday notifications
     */
    private val notificationSwitchOnCheckedListener =
        CompoundButton.OnCheckedChangeListener {_, isChecked ->
            val editor = sharedPrefs.edit()
            editor.putBoolean(BaseApp.NOTIFICATION_PREFERENCE, isChecked)
            editor.apply()
        }

    /**
     * View Listener to turn on/off the dark theme
     */
    private val themeOnClickListener = View.OnClickListener {
        settings_theme_switch.isChecked = !(settings_theme_switch.isChecked)
    }

    /**
     * Switch Listener to turn on/off the dark theme
     */
    private val themeSwitchOnCheckListener =
        CompoundButton.OnCheckedChangeListener{_, isChecked ->
            val editor = sharedPrefs.edit()
            editor.putInt(BaseApp.THEME_PREFERENCE, when(isChecked) {
                true -> BaseApp.THEME_DARK
                false -> BaseApp.THEME_LIGHT
            })
            editor.apply()
            when(isChecked) {
                true -> setTheme(R.style.DarkTheme)
                false -> setTheme(R.style.LightTheme)
            }
            val intent = intent
            finish()
            startActivity(intent)

            //Make sure to recreate view
            MainActivity.themeIsChanged = true
        }
}

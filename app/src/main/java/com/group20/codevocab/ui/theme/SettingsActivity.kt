package com.group20.codevocab.ui.theme

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.group20.codevocab.R
import com.group20.codevocab.data.local.TokenManager
import com.group20.codevocab.data.remote.ApiClient
import com.group20.codevocab.data.repository.AuthRepository
import com.group20.codevocab.databinding.ActivitySettingsBinding
import com.group20.codevocab.ui.auth.LoginActivity
import com.group20.codevocab.ui.profile.EditProfileActivity
import com.group20.codevocab.utils.ReminderReceiver
import com.group20.codevocab.viewmodel.BaseViewModelFactory
import com.group20.codevocab.viewmodel.UserViewModel
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var userViewModel: UserViewModel

    private var selectedHour = 9
    private var selectedMinute = 0

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleReminder()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createNotificationChannel()
        setupViewModel()
        observeUserData()
        setupListeners()

        userViewModel.fetchCurrentUser()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "REMINDER_CHANNEL",
                "Study Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for study notifications"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setupViewModel() {
        val apiService = ApiClient.getApiService()
        val repository = AuthRepository(apiService)
        val factory = object : BaseViewModelFactory<UserViewModel>() {
            override fun createViewModel(): UserViewModel = UserViewModel(repository)
        }
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    private fun observeUserData() {
        userViewModel.userData.observe(this) { user ->
            user?.let {
                binding.tvUserName.text = it.name
                binding.tvUserEmail.text = it.email
                if (!it.avatarUrl.isNullOrEmpty()) {
                    Glide.with(this).load(it.avatarUrl).placeholder(R.drawable.ic_user).circleCrop().into(binding.ivUserAvatar)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // ✅ Thêm sự kiện chuyển sang màn hình Edit Profile
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        val timeClickListener = { _: android.view.View -> showTimePicker() }
        binding.tvHour.setOnClickListener(timeClickListener)
        binding.tvMinute.setOnClickListener(timeClickListener)
        binding.tvAMPM.setOnClickListener(timeClickListener)

        binding.btnSetReminder.setOnClickListener {
            checkPermissionsAndSchedule()
        }

        binding.btnLogout.setOnClickListener {
            TokenManager(this).clearToken()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showTimePicker() {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedHour = hourOfDay
            selectedMinute = minute
            updateTimeUI()
        }, selectedHour, selectedMinute, false).show()
    }

    private fun updateTimeUI() {
        val displayHour = if (selectedHour % 12 == 0) 12 else selectedHour % 12
        val amPm = if (selectedHour < 12) "AM" else "PM"
        binding.tvHour.text = String.format("%02d", displayHour)
        binding.tvMinute.text = String.format("%02d", selectedMinute)
        binding.tvAMPM.text = amPm
    }

    private fun checkPermissionsAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                Toast.makeText(this, "Please grant Alarm permission", Toast.LENGTH_LONG).show()
                return
            }
        }

        scheduleReminder()
    }

    private fun scheduleReminder() {
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE, selectedMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        val timeStr = String.format("%02d:%02d %s",
            if (selectedHour % 12 == 0) 12 else selectedHour % 12,
            selectedMinute,
            if (selectedHour < 12) "AM" else "PM"
        )
        Toast.makeText(this, "Reminder scheduled for $timeStr", Toast.LENGTH_LONG).show()
    }
}
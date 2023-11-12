package org.sopt.official.feature.notification.enums

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import org.sopt.official.domain.entity.UserActiveState
import org.sopt.official.domain.entity.auth.UserStatus
import org.sopt.official.feature.attendance.AttendanceActivity
import org.sopt.official.feature.auth.AuthActivity
import org.sopt.official.feature.home.HomeActivity
import org.sopt.official.feature.mypage.MyPageActivity
import org.sopt.official.feature.notification.NotificationDetailActivity
import org.sopt.official.feature.notification.NotificationHistoryActivity
import org.sopt.official.stamp.SoptampActivity

enum class DeepLinkType(
    val link: String
) {
    HOME("home") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return getHomeIntent(context, userStatus)
        }
    },
    NOTIFICATION_LIST("home/notification") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return userStatus.setIntent(
                context,
                Intent(context, NotificationHistoryActivity::class.java)
            )
        }
    },
    NOTIFICATION_DETAIL("home/notification/detail") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            val notificationId = deepLink.toUri().getQueryParameter("id")?.toLong() ?: 0
            return userStatus.setIntent(
                context,
                NotificationDetailActivity.getIntent(
                    context,
                    NotificationDetailActivity.StartArgs(notificationId)
                )
            )
        }
    },
    MY_PAGE("home/mypage") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return userStatus.setIntent(
                context,
                MyPageActivity.getIntent(
                    context,
                    MyPageActivity.StartArgs(UserActiveState.valueOf(userStatus.name))
                )
            )
        }
    },
    ATTENDANCE("home/attendance") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return userStatus.setIntent(
                context,
                Intent(context, AttendanceActivity::class.java)
            )
        }
    },
    ATTENDANCE_MODAL("home/attendance/attendance-modal") {
        override fun getIntent( // TODO
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return userStatus.setIntent(
                context,
                AttendanceActivity.getIntent(
                    context,
                    AttendanceActivity.StartArgs(true)
                )
            )
        }
    },
    SOPTAMP("home/soptamp") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return userStatus.setIntent(
                context,
                Intent(context, SoptampActivity::class.java)
            )
        }
    },
    SOPTAMP_ENTIRE_RANKING("home/soptamp/entire-ranking") {
        override fun getIntent( // TODO
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            TODO("Not yet implemented")
        }
    },
    SOPTAMP_CURRENT_GENERATION_RANKING("home/soptamp/current-generation-ranking") {
        override fun getIntent( // TODO
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            TODO("Not yet implemented")
        }
    },
    UNKNOWN("unknown-deep-link") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return getHomeIntent(context, userStatus, true)
        }
    };

    abstract fun getIntent(
        context: Context,
        userStatus: UserStatus,
        deepLink: String,
    ): Intent

    companion object {
        fun UserStatus.setIntent(
            context: Context,
            intent: Intent,
        ): Intent {
            return when (this == UserStatus.UNAUTHENTICATED) {
                true -> AuthActivity.newInstance(context)
                false -> intent
            }
        }
        fun getHomeIntent(
            context: Context,
            userStatus: UserStatus,
            isUnknownDeepLink: Boolean = false
        ): Intent {
            return when (userStatus == UserStatus.UNAUTHENTICATED) {
                true -> AuthActivity.newInstance(context)
                false -> HomeActivity.getIntent(
                    context,
                    HomeActivity.StartArgs(
                        userStatus = userStatus,
                        isUnknownDeepLink = isUnknownDeepLink
                    )
                )
            }
        }

        operator fun invoke(deepLink: String): DeepLinkType {
            val link = deepLink.split("?")[0]
            return entries.find { it.link == link } ?: run {
                when (deepLink.isBlank()) {
                    true -> NOTIFICATION_DETAIL
                    false -> UNKNOWN
                }
            }
        }
    }
}
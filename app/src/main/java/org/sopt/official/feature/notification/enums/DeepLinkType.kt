package org.sopt.official.feature.notification.enums

import android.content.Context
import android.content.Intent
import org.sopt.official.auth.model.UserActiveState
import org.sopt.official.auth.model.UserStatus
import org.sopt.official.common.util.extractQueryParameter
import org.sopt.official.feature.attendance.AttendanceActivity
import org.sopt.official.feature.auth.AuthActivity
import org.sopt.official.feature.home.HomeActivity
import org.sopt.official.feature.mypage.mypage.MyPageActivity
import org.sopt.official.feature.notification.NotificationDetailActivity
import org.sopt.official.feature.notification.NotificationHistoryActivity
import org.sopt.official.stamp.SoptampActivity
import timber.log.Timber

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
            val notificationId = deepLink.extractQueryParameter("id")
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
    SOPTAMP_CURRENT_GENERATION_RANKING("home/soptamp/current-generation-ranking") {
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
    UNKNOWN("unknown-deep-link") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return getHomeIntent(context, userStatus, UNKNOWN)
        }
    },
    EXPIRED("expired") {
        override fun getIntent(
            context: Context,
            userStatus: UserStatus,
            deepLink: String,
        ): Intent {
            return getHomeIntent(context, userStatus, EXPIRED)
        }
    };

    abstract fun getIntent(
        context: Context,
        userStatus: UserStatus,
        deepLink: String,
    ): Intent

    companion object {
        private fun UserStatus.setIntent(
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
            deepLinkType: DeepLinkType? = null
        ): Intent {
            return userStatus.setIntent(
                context,
                HomeActivity.getIntent(
                    context,
                    HomeActivity.StartArgs(
                        userStatus = userStatus,
                        deepLinkType = deepLinkType
                    )
                )
            )
        }

        operator fun invoke(deepLink: String): DeepLinkType {
            return try {
                val link = deepLink.split("?")[0]
                entries.find { it.link == link } ?: UNKNOWN
            } catch (exception: Exception) {
                Timber.e(exception)
                UNKNOWN
            }
        }
    }
}
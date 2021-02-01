package com.mortarifabio.desafiofirebase.utils

class Constants {
    object Authentication {
        const val AUTH_LOG_KEY = "Firebase Auth"
        const val SHARED_PREFERENCES_FILENAME = "com.mortarifabio.desafiofirebase.sharedpreferences"
        const val SHARED_PREFERENCES_EMAIL_KEY = "user_email"
    }

    object Analytics {
        const val ANALYTICS_MANUAL_LOGIN_EVENT = "user_login"
        const val ANALYTICS_AUTOMATIC_LOGIN_EVENT = "automatic_login"
        const val ANALYTICS_CREATE_ACCOUNT_EVENT = "user_create_account"
        const val ANALYTICS_REGISTER_USER_EVENT = "user_register"
        const val ANALYTICS_ADD_GAME_EVENT = "game_add"
        const val ANALYTICS_UPDATE_GAME_EVENT = "game_update"
        const val ANALYTICS_GAME_DETAILS_EVENT = "game_details"
    }

    object Firestore {
        const val FIRESTORE_LOG_KEY = "Firebase Firestore"
        const val FIRESTORE_COLLECTION_USERS = "users"
        const val FIRESTORE_COLLECTION_USER_GAMES = "games"
        const val FIRESTORE_FIRST_PAGE = 1
        const val FIRESTORE_PAGE_SIZE = 20
    }

    object Storage {
        const val STORAGE_LOG_KEY = "Firebase Storage"
    }

    object Intent {
        const val INTENT_GAME_KEY = "game"
    }

}
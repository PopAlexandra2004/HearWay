package ro.utcn.uid.hearway.common

enum class HearwayAppState {
    INIT,
    LOAD_TTS,
    DASHBOARD,
    COMMUNICATE,
    EMERGENCY,
    ROUTE_PLANNING,
    ACTIVE_NAVIGATION,
    FIND_STOPS,
    REQUEST_HELP,
    SAVE_ROUTE,
    SET_REMINDER,
    FUTURE_NAVIGATION,
    ERROR,
    EXIT
}
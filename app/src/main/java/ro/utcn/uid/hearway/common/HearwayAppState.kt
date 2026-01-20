package ro.utcn.uid.hearway.common

enum class HearwayAppState {
    INIT,
    LOAD_TTS,
    DASHBOARD,
    COMMUNICATE,
    EMERGENCY,
    ACTIVE_NAVIGATION,  // New state for Task 3
    FUTURE_NAVIGATION,
    ERROR,
    EXIT
}
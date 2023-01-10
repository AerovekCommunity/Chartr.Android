package org.aerovek.chartr.ui.passcode

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hadilq.liveevent.LiveEvent
import org.aerovek.chartr.ui.AppConstants
import org.aerovek.chartr.ui.BaseViewModel

class PassCodeViewModel (app: Application, private val sharedPreferences: SharedPreferences) : BaseViewModel(app) {

    private val _enterPinLabel = MutableLiveData("Enter PIN")
    val enterPinLabel: LiveData<String> = _enterPinLabel
    val pinCodePair = MutableLiveData(Pair(0, PinPadType.Blank))
    val entryComplete = LiveEvent<Unit>()
    val insecurePinDetected = LiveEvent<Unit>()
    val invalidPin = LiveEvent<Unit>()
    val clearDots = LiveEvent<Unit>()
    var isFirstEntry = true
    var enteredPin = ""
    var savedFirstEntry = ""


    // Use this to determine if user is creating a new PIN,
    // or just unlocking the app or signing a transaction
    private val isNewWallet = !sharedPreferences.contains(AppConstants.UserPrefsKeys.USER_PIN)

    // Simple regex to catch repeating digits or a few others like 123456
    private val regex = Regex("\\b(\\d)\\1+\\b|123456|234567|345678|456789")

    fun onClick(pinPadModel: PinPadModel) {
        when(pinPadModel.type) {
            PinPadType.BackSpace -> {
                if (enteredPin.isNotEmpty()) {
                    enteredPin = enteredPin.dropLast(1)
                    pinCodePair.postValue(Pair((enteredPin.length), PinPadType.BackSpace))
                }
            }
            PinPadType.Blank -> {
                // NoOp for now
                println("Backspace Pressed")
            }
            PinPadType.Value -> {
                enteredPin += pinPadModel.value

                if (enteredPin.length == 6) {
                    if (isNewWallet) {
                        validateNewWalletPin()
                    } else {
                        validateForExistingPin()
                    }
                    reset()
                } else {
                    pinCodePair.postValue(Pair(enteredPin.length - 1, PinPadType.Value))
                }
            }
        }
    }

    private fun validateNewWalletPin() {
        if (isFirstEntry) {
            if (regex.matches(enteredPin)) {
                insecurePinDetected.postValue(Unit)
            } else {
                _enterPinLabel.postValue("Enter PIN again to confirm")
                isFirstEntry = false
                savedFirstEntry = enteredPin
            }
        } else {
            if (enteredPin == savedFirstEntry) {
                // Store the PIN temporarily, this will be copied over to the
                // real pin entry storage after they finish the setup process.
                sharedPreferences.edit().putString(AppConstants.UserPrefsKeys.USER_TEMP_PIN, enteredPin).apply()

                entryComplete.postValue(Unit)
            } else {
                invalidPin.postValue(Unit)
            }
        }
    }

    private fun validateForExistingPin() {
        if (enteredPin == sharedPreferences.getString(AppConstants.UserPrefsKeys.USER_PIN, "")) {
            entryComplete.postValue(Unit)
        } else {
            invalidPin.postValue(Unit)
        }
    }

    private fun reset() {
        enteredPin = ""
        clearDots.postValue(Unit)
    }
}

package at.jku.yourmemorylane.ui.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FriendsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the friends fragment"
    }
    val text: LiveData<String> = _text
}
package com.sibdev.firefighter.viewModels

import android.app.Activity
import android.content.Context
import androidx.compose.material.contentColorFor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.sibdev.firefighter.db.Repository
import com.sibdev.firefighter.db.SharedPref
import com.sibdev.firefighter.models.User
import com.sibdev.firefighter.utils.foregroundStartService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginScreenViewModel(val context: Context):ViewModel() {
    private val auth = Firebase.auth
    private val _signedIn = MutableStateFlow(false)
    val signedIn:StateFlow<Boolean> = _signedIn
    val sharedPref=SharedPref(context = context)
    fun signIn(task: Task<GoogleSignInAccount>?) {
        if (task != null) {
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(context = context, account)
            }
        }
    }
    fun firebaseAuthWithGoogle(context: Context, account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            sharedPref.token=it
        }
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if(task.isSuccessful)
                {
                    addUser(account)
                  _signedIn.value=true
                }
            }
    }
     fun addUser(account: GoogleSignInAccount) {
        val user = User(uId = auth.uid!!, email = account.email!!, displayName = account.displayName!! , imageUrl = account.photoUrl.toString(), token = sharedPref.token )
        viewModelScope.launch(Dispatchers.IO) {
            Repository.userById(auth.uid!!).set(user).addOnSuccessListener {
                val sharedPref = SharedPref(context = context)
                sharedPref.setEmail(user.email)
                sharedPref.setName(user.displayName)
                if(user.imageUrl!=null)
                sharedPref.setImageUrl(user.imageUrl)
                context.foregroundStartService("Start")
            }
        }
    }
}
class LoginViewModelProvider(val context: Context) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = LoginScreenViewModel(context = context) as T
}

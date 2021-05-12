package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.data.LoginInfo
import com.hunterlc.hmusic.network.repository.Repository

class LoginByCellphoneViewModel: ViewModel() {

    private val loginChangeLiveData = MutableLiveData<LoginInfo>()

    var loginLiveData = Transformations.switchMap(loginChangeLiveData) { login ->
        Repository.loginByCellphone(login.phone,login.countryCode,login.md5_password)
    }

    fun loginByCellphone(phone: String, countrycode: Int, md5_password: String) {
        val login = LoginInfo(phone,86, md5_password)
        loginChangeLiveData.value = login
    }
}
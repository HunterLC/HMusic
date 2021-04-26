package com.hunterlc.hmusic.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hunterlc.hmusic.data.LoginInfo
import com.hunterlc.hmusic.network.repository.Repository
import com.hunterlc.hmusic.util.LogUtil

class LoginViewModel : ViewModel() {
    val userLoginInfoLiveData = MutableLiveData<LoginInfo>()

    var userDetailLiveData = Transformations.switchMap(userLoginInfoLiveData) { loginInfo ->
        Repository.loginByCellphone(loginInfo.phone, loginInfo.countrycode, loginInfo.md5_password)

    }

    fun loginByCellphone(phone: String, countrycode: Int, password: String){
        //val md5_password = encodeMD5(password)
        LogUtil.e("LoginViewModel-loginByCellphone", "md5_password")
        userLoginInfoLiveData.value = LoginInfo(phone, countrycode, password)
    }
}
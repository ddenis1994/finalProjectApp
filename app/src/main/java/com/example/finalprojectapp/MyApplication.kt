package com.example.finalprojectapp

import android.app.Application
import com.example.finalprojectapp.crypto.HashBuilder
import com.example.finalprojectapp.crypto.LocalCryptography
import dagger.Component
import java.security.MessageDigest

@Component
interface ApplicationComponent{
    fun getHashBuilder():HashBuilder
    fun getLocalLocalCryptography(): LocalCryptography
    fun inject(messageDigest2: MessageDigest)
    fun inject2(hashBuilder: HashBuilder)
}

class MyApplication: Application() {

    val appComponent = DaggerApplicationComponent.create()

}
package com.example.finalprojectapp.utils

class MyFireBaseSingleton private constructor() {
    private object HOLDER {


        val INSTANCE = MyFireBaseSingleton()
    }

    companion object {
        val instance: MyFireBaseSingleton by lazy { HOLDER.INSTANCE }
    }
}
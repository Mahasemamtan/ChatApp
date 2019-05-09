package com.example.chatapp.model

class User (val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this ("", "", "")
}
package com.riezki.storyapp.utils

import com.riezki.storyapp.model.response.*

object DataDummy {

    fun generateDummyPagedStoryResponse(page: Int, size: Int) : List<ListStoryItemResponse> {
        val listStory: MutableList<ListStoryItemResponse> = arrayListOf()

        for (i in 0..100) {
            val story = ListStoryItemResponse(
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                name = "Dimas",
                description = "Lorem Ipsum",
                id = "story-FvU4u0Vp2S3PMsFg"
            )

            listStory.add(story)
        }
        /**
         * mencari halaman berdasarkan no index, jika jika halaman asal (from index) adalah 3 maka nomor index yang didapat adalah no index ke-20 (dihitung dari 0)
         * dan jika halaman tujuan (to index ) adalah 4 maka nomor index yang didapat adalah no index ke-40 (dihitung dari 0)
         * berdasarkan algoritma dibawah
         */
        return listStory.subList((page -1) * size, (page - 1) * size + size)
    }

    fun generateAddNewStoryResponse(): AddNewStoryResponse {
        return AddNewStoryResponse(
            error = false,
            message = "success"
        )
    }

    fun generateRegisterStoryResponse() : RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "User Created"
        )
    }

    fun generateLoginStoryResponse() : LoginResponse {
        val login = LoginResult(
            name = "kiki",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLWRlX29wd1dmN0s4QVhNSTkiLCJpYXQiOjE2NjcwNDYxMzN9._TmFRjqq3BL8R--c1mIDTB2UwaJfFDCv_kt8lxNfnMU",
            userId = "story-FvU4u0Vp2S3PMsFg"
        )
        return LoginResponse(
            loginResult = login,
            error = false,
            message = "success"
        )
    }

    fun generateDummyStoryResponse() : List<ListStoryItemResponse> {
        val listStory = ArrayList<ListStoryItemResponse>()

        for (i in 0..10) {
            val story = ListStoryItemResponse(
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                name = "Dimas",
                lat = -10.212,
                lon =  -16.002,
                description = "Lorem Ipsum",
                id = "story-FvU4u0Vp2S3PMsFg"
            )

            listStory.add(story)
        }
        return listStory
    }

    fun generateImageResponse() : ListStoryResponse {
        val listStory = ArrayList<ListStoryItemResponse>()

        for (i in 0..10) {
            val story = ListStoryItemResponse(
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                name = "Dimas",
                description = "Lorem Ipsum",
                id = "story-FvU4u0Vp2S3PMsFg"
            )

            listStory.add(story)
        }

        return ListStoryResponse(listStory, false, "success")
    }
}
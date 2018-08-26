package com.njust.helper.library.search

class LibSearchItemVm(
        bean: LibSearchBean,
        val position: Int
) {
    val title: String = bean.title
    val author: String = bean.author
    val press: String = bean.press
    val id: String = bean.id
}

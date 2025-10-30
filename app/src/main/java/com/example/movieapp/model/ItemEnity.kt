package com.example.movieapp.model


sealed interface MultiViewItem {
    val id: String

    data class LoadingMore(override val id: String = "LoadingMore") : MultiViewItem

    data class CategoryHeader(
        override val id: String = "CategoryHeader",
        var list: List<CategoryMovie> = emptyList()
    ) :
        MultiViewItem {
        var index = -1


    }

    data class CountryHeader(
        override val id: String = "CountryHeader",
        var list: List<CountryMovie> = emptyList()
    ) :
        MultiViewItem {
        var index = -1

        companion object {
            val countriesList = emptyList<String>()
        }

    }

    data class YearHeader(override val id: String = "YearHeader") : MultiViewItem {
        var index = -1

        companion object {
            val yearsList = listOf(
                "2025",
                "2024",
                "2023",
                "2022",
                "2021",
                "2020",
                "2019",
                "2018",
                "2017",
                "2016",
                "2015",
                "2014",
                "2013",
                "2012",
                "2011",
                "2010",
                "2009",
                "2008",
                "2007",
                "2006",
                "2005",
                "2004",
                "2003",
                "2002",
                "2001",
                "2000"
            )

        }
    }

    data class Empty(override val id: String = "Empty") : MultiViewItem

}




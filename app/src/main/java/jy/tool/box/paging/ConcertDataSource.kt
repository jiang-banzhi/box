package jy.tool.box.paging

import androidx.paging.PositionalDataSource

/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/25.
 * @desciption :
 * @version :
 *</pre>
 */
class ConcertDataSource : PositionalDataSource<Concert>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Concert>) {
        callback.onResult(fetchItems(params.startPosition, params.loadSize))
    }
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Concert>) {

        callback.onResult(fetchItems(0, 20), 0, 58)
    }

    private fun fetchItems(startPosition: Int, pageSize: Int): List<Concert> {
        val list = mutableListOf<Concert>()

        for (i in startPosition until startPosition + pageSize) {
            val concert = Concert("content = $i", "author = $i", "title = $i")
            list.add(concert)
        }
        return list
    }
}
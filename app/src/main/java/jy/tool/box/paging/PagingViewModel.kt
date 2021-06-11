package jy.tool.box.paging

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.junfa.growthcompass.terminal.base.BaseViewModel




/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/25.
 * @desciption :
 * @version :
 *</pre>
 */
class PagingViewModel(application: Application) : BaseViewModel(application) {
    private var convertList: LiveData<PagedList<Concert>>? = null
    private var concertDataSource: DataSource<Int, Concert>? = null
    init {
        val concertFactory = ConcertDataSourceFactory()
        concertDataSource = concertFactory.create()
        convertList = LivePagedListBuilder(concertFactory, 20).build()
    }

    fun invalidateDataSource() {
        concertDataSource?.invalidate()
    }

    fun getConvertList(): LiveData<PagedList<Concert>>? {
        return convertList
    }
}
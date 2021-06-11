package jy.tool.box.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource



/**
 *<pre>
 * @author : jiang
 * @time : 2021/4/25.
 * @desciption :
 * @version :
 *</pre>
 */
class ConcertDataSourceFactory : DataSource.Factory<Int, Concert>() {
    private val mSourceLiveData = MutableLiveData<ConcertDataSource>()
    override fun create(): DataSource<Int, Concert> {
        val concertDataSource = ConcertDataSource()
        mSourceLiveData.postValue(concertDataSource)
        return concertDataSource

    }

}
package jy.tool.box.menu

import android.content.ComponentName
import android.content.Context

/**
 *<pre>
 * @author : jiang
 * @time : 2022/2/15.
 * @desciption :
 * @version :
 *</pre>
 */
data class MenuInfo(
    var menuName: String = "",
    var cls: Class<*>? = null
) {

}

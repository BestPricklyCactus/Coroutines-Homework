package otus.homework.coroutines

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import otus.homework.coroutines.CrashMonitor.trackWarning
import java.net.SocketTimeoutException


class CatsPresenter(
    private val catsService: CatsService,
    private val onShowToast: (String?) -> Unit
) {

    private var _catsView: ICatsView? = null
    private val presenterScope = CoroutineScope(Dispatchers.Main + CoroutineName("CatsCoroutine"))
    fun onInitComplete() {
        presenterScope.launch {
            try {
                val fact = catsService.getCatFact()
                _catsView?.populate(fact)
            } catch (e: SocketTimeoutException) {
                println("Caught $e")
                onShowToast("Не удалось получить ответ от сервера")

            } catch (e: Exception) {
                trackWarning(e.message)
                onShowToast(e.message)
            }
        }
    }

    fun attachView(catsView: ICatsView) {
        _catsView = catsView
    }

    fun detachView() {
        _catsView = null
    }

    fun onStop() {
        presenterScope.cancel()
    }
}
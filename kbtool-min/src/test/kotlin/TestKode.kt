import com.github.jchanghong.kotlin.toDateJdk7OrNull
import com.github.jchanghong.kotlin.toStrOrNow

class TestKode {
}

fun main() {

//    2021-03-19T10:31:41.685+08:00
	val s = "2021-03-19T10:31:41.685+08:00"
	val orNull = s.toDateJdk7OrNull()
	println(orNull.toStrOrNow())
}

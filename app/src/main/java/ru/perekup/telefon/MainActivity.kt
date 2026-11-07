// ПЕРЕПРОДАЖА ТЕЛЕФОНОВ АВИТО — симулятор для Android (Kotlin, без внешних библиотек). Версия 1.0.0
package ru.perekup.telefon

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import java.util.Random
import java.util.WeakHashMap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

const val CH = "@perekuptelefon"
const val LINK = "https://t.me/perekuptelefon"
const val PASS = "zcn18-122"
const val VERSION = "1.0.0"
const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

typealias Cb = (String) -> Unit

fun rgbf(r: Double, g: Double, b: Double): Int = Color.rgb((r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())

val CARD = Color.rgb(33, 38, 51)
val PEN = mapOf("screen" to 0.22, "battery" to 0.10, "body" to 0.07, "board" to 0.30)
val REP = mapOf("screen" to Pair(600.0, 0.14), "battery" to Pair(300.0, 0.06), "body" to Pair(200.0, 0.04), "board" to Pair(900.0, 0.18))
val NAME = mapOf("screen" to "экран", "battery" to "батарея", "body" to "корпус", "board" to "плата")
val BOTS = listOf("Алексей_К", "Мария88", "dmitry_pro", "Ольга П.", "Игорь", "Sergey_74", "Анна_М", "Влад", "Катя_Z", "Roman2000", "Юля", "Павел_Н")
val TRA = listOf("Тихий", "Ночной", "Светлый", "Тёплый", "Дальний", "Мягкий", "Лёгкий")
val TRB = listOf("рассвет", "дождь", "город", "ветер", "океан")
val TRK = List(35) { "Трек ${it + 1}. ${TRA[it % 7]} ${TRB[it / 7]}" }
val TABS = listOf("avito" to "Авито", "bm" to "Чёрный", "inv" to "Склад", "auc" to "Аукцион", "more" to "Ещё")

val THEMES = linkedMapOf<String, Int?>(
    "Синяя" to rgbf(0.13, 0.45, 0.9), "Зелёная" to rgbf(0.1, 0.65, 0.35), "Фиолетовая" to rgbf(0.55, 0.3, 0.85),
    "Оранжевая" to rgbf(0.95, 0.5, 0.1), "Красная" to rgbf(0.85, 0.2, 0.25), "Радужная" to null
)

val BC = mapOf(
    "Nokia" to rgbf(0.1, 0.35, 0.75), "Motorola" to rgbf(0.1, 0.45, 0.85), "Ericsson" to rgbf(0.1, 0.3, 0.6),
    "Sony Ericsson" to rgbf(0.2, 0.2, 0.2), "Siemens" to rgbf(0.0, 0.55, 0.6), "BlackBerry" to rgbf(0.15, 0.15, 0.15),
    "LG" to rgbf(0.75, 0.1, 0.3), "Apple" to rgbf(0.35, 0.35, 0.4), "Samsung" to rgbf(0.1, 0.25, 0.65),
    "HTC" to rgbf(0.4, 0.7, 0.1), "Sony" to rgbf(0.15, 0.15, 0.18), "Xiaomi" to rgbf(1.0, 0.4, 0.0),
    "Huawei" to rgbf(0.8, 0.1, 0.15), "Honor" to rgbf(0.1, 0.45, 0.9), "Meizu" to rgbf(0.0, 0.6, 0.8),
    "OnePlus" to rgbf(0.9, 0.1, 0.15), "Google" to rgbf(0.25, 0.5, 0.9), "Realme" to rgbf(0.9, 0.75, 0.1),
    "Poco" to rgbf(0.95, 0.75, 0.0), "Nothing" to rgbf(0.2, 0.2, 0.2), "Asus" to rgbf(0.0, 0.35, 0.65),
    "Infinix" to rgbf(0.1, 0.6, 0.4)
)

const val DATA = """Nokia:1011/1992/3500;8110/1996/3000;5110/1998/1800;3210/1999/1800;3310/2000/2000;6310i/2002/2200;1100/2003/900;N73/2006/1800;N95/2007/4000;Lumia 920/2012/2500;Lumia 1020/2013/3500;8/2017/7000
Motorola:StarTAC/1996/4000;RAZR V3/2004/3500;Moto G/2013/2500;Edge 40/2023/27000
Ericsson:GH337/1997/1500
Sony Ericsson:T68i/2002/1500;K750i/2005/1500
Siemens:S10/1997/1200;C55/2003/700
BlackBerry:Bold 9000/2008/2500
LG:KG800 Chocolate/2006/1500;Nexus 5/2013/3500;G3/2014/3500;V30/2017/9000
Apple:iPhone 2G/2007/6000;iPhone 3GS/2009/2500;iPhone 4/2010/3000;iPhone 5/2012/3500;iPhone 5s/2013/4500;iPhone 6/2014/5500;iPhone 6s/2015/7000;iPhone 7/2016/9000;iPhone 8/2017/11000;iPhone X/2017/15000;iPhone XR/2018/17000;iPhone 11/2019/22000;iPhone 12/2020/32000;iPhone 13/2021/40000;iPhone 14/2022/48000;iPhone 15 Pro/2023/75000;iPhone 16/2024/75000
Samsung:SGH-D900/2007/1500;Galaxy S II/2011/2000;Galaxy S3/2012/2500;Galaxy S4/2013/3000;Galaxy S5/2014/3500;Galaxy S7/2016/6000;Galaxy S8/2017/9000;Galaxy S9/2018/11000;Galaxy S10/2019/15000;Galaxy Note 9/2018/14000;Galaxy A50/2019/9000;Galaxy S20/2020/20000;Galaxy S21/2021/28000;Galaxy S22 Ultra/2022/45000;Galaxy S24 Ultra/2024/80000;Galaxy Z Flip 5/2023/50000;Galaxy A54/2023/22000
HTC:Desire/2010/1500;One M7/2013/3000;One M8/2014/4000
Sony:Xperia Z/2013/3000;Xperia Z3/2014/4500;Xperia XZ2/2018/10000;Xperia 1 V/2023/60000
Xiaomi:Mi 3/2013/2500;Redmi Note 3/2016/4500;Redmi Note 7/2019/7000;Mi 9/2019/12000;Redmi Note 9/2020/9000;12/2021/28000;Redmi Note 12/2023/15000;14/2024/50000
Huawei:Ascend P6/2013/2500;P8/2015/4500;P20 Pro/2018/12000;P30 Pro/2019/17000;P50 Pro/2021/38000
Honor:10/2018/8000;20/2019/11000;Magic5 Pro/2023/45000
Meizu:MX4/2014/3500
OnePlus:One/2014/4500;6T/2018/12000;7 Pro/2019/16000;11/2023/42000
Google:Nexus 6P/2015/5000;Pixel 3/2018/9000;Pixel 4a/2020/13000;Pixel 7/2022/30000;Pixel 8 Pro/2023/50000
Realme:6/2020/9000;GT 5/2023/32000
Poco:F2 Pro/2020/16000;X5 Pro/2023/20000
Nothing:Phone (1)/2022/25000
Asus:ROG Phone 7/2023/55000
Infinix:Note 30/2023/11000"""

class Spec(val brand: String, val model: String, val year: Int, val price: Int)

val CAT: List<Spec> = DATA.lines().filter { it.contains(":") }.flatMap { ln ->
    val i = ln.indexOf(':')
    val br = ln.substring(0, i)
    ln.substring(i + 1).split(";").map { item ->
        val q = item.split("/")
        Spec(br, q[0], q[1].toInt(), q[2].toInt())
    }
}

fun money(n: Int): String {
    val s = abs(n).toString()
    val sb = StringBuilder()
    for (i in s.indices) {
        if (i > 0 && (s.length - i) % 3 == 0) sb.append('\u00A0')
        sb.append(s[i])
    }
    return (if (n < 0) "-" else "") + sb.toString() + "\u00A0руб."
}

class Phone(
    val id: Long, val brand: String, val model: String, val year: Int,
    val base: Int, val defects: MutableList<String>, val seed: Int
) {
    var cost = 0
    var ask = 0
    var min = 0
    var tries = 0
    var buyerW = 0

    fun toJson(): JSONObject {
        val o = JSONObject()
        o.put("id", id); o.put("brand", brand); o.put("model", model); o.put("year", year)
        o.put("base", base); o.put("d", JSONArray(defects)); o.put("seed", seed); o.put("cost", cost)
        return o
    }

    companion object {
        fun fromJson(o: JSONObject): Phone {
            val d = ArrayList<String>()
            val a = o.getJSONArray("d")
            for (i in 0 until a.length()) d.add(a.getString(i))
            val p = Phone(o.getLong("id"), o.getString("brand"), o.getString("model"), o.getInt("year"), o.getInt("base"), d, o.getInt("seed"))
            p.cost = o.optInt("cost")
            return p
        }
    }
}

class Auction(val p: Phone, var price: Int, var lead: String, var idle: Int)

object Skin {
    var color: Int = Color.rgb(33, 115, 230)
    val btns: MutableSet<Btn> = Collections.newSetFromMap(WeakHashMap<Btn, Boolean>())
    fun paint(c: Int) {
        color = c
        for (b in btns) b.gd.setColor(c)
    }
}

class Btn(ctx: Context, label: String, size: Float = 13f) : Button(ctx) {
    val gd = GradientDrawable()

    init {
        text = label
        setAllCaps(false)
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        typeface = Typeface.DEFAULT_BOLD
        gd.cornerRadius = 8f * ctx.resources.displayMetrics.density
        gd.setColor(Skin.color)
        background = RippleDrawable(ColorStateList.valueOf(0x55FFFFFF), gd, null)
        stateListAnimator = null
        minHeight = 0
        minimumHeight = 0
        val pd = (6f * ctx.resources.displayMetrics.density).toInt()
        setPadding(pd, pd, pd, pd)
        Skin.btns.add(this)
    }
}

class PhoneView(ctx: Context, private val p: Phone) : View(ctx) {
    private val pt = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rc = RectF()

    private fun dark(c: Int, f: Float): Int =
        Color.rgb((Color.red(c) * f).toInt(), (Color.green(c) * f).toInt(), (Color.blue(c) * f).toInt())

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val hh = height.toFloat()
        val c = BC[p.brand] ?: Color.rgb(102, 102, 115)
        val ph = hh * 0.92f
        val pw = min(w * 0.9f, ph * (if (p.year < 2007) 0.45f else 0.5f))
        val x0 = (w - pw) / 2f
        val y0 = (hh - ph) / 2f

        // прямоугольник в долях телефона: fx, fw — доли ширины; fy (снизу), fh — доли высоты
        fun box(fx: Float, fy: Float, fw: Float, fh: Float) {
            val l = x0 + pw * fx
            val b = y0 + ph * (1f - fy)
            rc.set(l, b - ph * fh, l + pw * fw, b)
        }

        pt.style = Paint.Style.FILL
        pt.color = dark(c, 0.55f)
        box(0f, 0f, 1f, 1f)
        canvas.drawRoundRect(rc, pw * 0.16f, pw * 0.16f, pt)
        if (p.year < 2007) {
            pt.color = Color.rgb(153, 204, 166)
            box(0.14f, 0.58f, 0.72f, 0.26f)
            canvas.drawRect(rc, pt)
            pt.color = Color.rgb(217, 217, 224)
            for (r in 0 until 4) {
                for (q in 0 until 3) {
                    box(0.14f + 0.25f * q, 0.43f - 0.1f * r, 0.2f, 0.07f)
                    canvas.drawRoundRect(rc, 4f, 4f, pt)
                }
            }
        } else {
            pt.color = Color.rgb(10, 13, 20)
            box(0.05f, 0.03f, 0.9f, 0.94f)
            canvas.drawRoundRect(rc, pw * 0.13f, pw * 0.13f, pt)
            pt.color = Color.argb(153, Color.red(c), Color.green(c), Color.blue(c))
            box(0.09f, 0.08f, 0.82f, 0.82f)
            canvas.drawRect(rc, pt)
            pt.color = Color.BLACK
            val n = 1 + (if (p.year > 2016) 1 else 0) + (if (p.year > 2019) 1 else 0)
            for (i in 0 until n) {
                val l = x0 + pw * (0.3f + 0.16f * i)
                val b = y0 + ph * 0.1f
                rc.set(l, b - pw * 0.1f, l + pw * 0.1f, b)
                canvas.drawOval(rc, pt)
            }
        }
        val d = p.defects
        if (d.contains("screen")) {
            pt.style = Paint.Style.STROKE
            pt.strokeWidth = 2.5f
            pt.color = Color.WHITE
            val path = Path()
            path.moveTo(x0 + pw * 0.2f, y0 + ph * 0.2f)
            path.lineTo(x0 + pw * 0.5f, y0 + ph * 0.5f)
            path.lineTo(x0 + pw * 0.4f, y0 + ph * 0.7f)
            path.lineTo(x0 + pw * 0.7f, y0 + ph * 0.85f)
            canvas.drawPath(path, pt)
            pt.style = Paint.Style.FILL
        }
        if (d.contains("body")) {
            pt.style = Paint.Style.STROKE
            pt.strokeWidth = 3f
            pt.color = Color.rgb(230, 230, 230)
            canvas.drawLine(x0 + pw * 0.02f, y0 + ph * 0.55f, x0 + pw * 0.02f, y0 + ph * 0.8f, pt)
            pt.style = Paint.Style.FILL
        }
        if (d.contains("battery")) {
            pt.color = Color.rgb(230, 38, 38)
            box(0.75f, 0.02f, 0.15f, 0.03f)
            canvas.drawRect(rc, pt)
        }
    }
}

// Собственная музыка: без слов и без авторских прав. 35 композиций создаются по номеру.
fun synth(idx: Int, f: File) {
    val r = Random((idx * 7919 + 13).toLong())
    val sr = 11025
    val beat = 60.0 / intArrayOf(68, 76, 84, 92, 100, 110)[r.nextInt(6)]
    val root = doubleArrayOf(110.0, 123.5, 130.8, 146.8, 164.8, 98.0)[r.nextInt(6)]
    val scales = arrayOf(intArrayOf(0, 2, 4, 7, 9), intArrayOf(0, 3, 5, 7, 10), intArrayOf(0, 2, 3, 7, 8), intArrayOf(0, 2, 4, 5, 7, 9, 11))
    val sc = scales[r.nextInt(4)]
    fun fr(d: Int, o: Int = 0): Double = root * 2.0.pow(o + (sc[d % sc.size] + 12 * (d / sc.size)) / 12.0)
    val bars = 8
    val tot = (sr * beat * 4 * bars).toInt() + sr
    val buf = FloatArray(tot)
    fun note(fq: Double, st: Double, du: Double, v: Double) {
        val s = (st * sr).toInt()
        val n = (du * sr).toInt()
        val k = 6.2832 * fq / sr
        for (i in 0 until n) {
            if (s + i >= tot) break
            val e = min(1.0, i / 200.0) * (1.0 - i.toDouble() / n)
            buf[s + i] += (v * e * (sin(k * i) + 0.3 * sin(2 * k * i))).toFloat()
        }
    }
    val progs = arrayOf(intArrayOf(0, 3, 4, 3), intArrayOf(0, 2, 3, 4), intArrayOf(0, 4, 3, 2))
    val prog = progs[r.nextInt(3)]
    val arp = intArrayOf(0, 2, 4, 2)
    val steps = intArrayOf(-2, -1, 1, 2)
    var d = r.nextInt(5)
    for (b in 0 until bars) {
        val ch = prog[b % 4]
        val t0 = b * beat * 4
        note(fr(ch, 0), t0, beat * 3.8, 0.5)
        for (q in 0 until 8) {
            note(fr(ch + arp[q % 4], 1), t0 + q * beat / 2, beat * 0.9, 0.18)
            if (r.nextDouble() < 0.5) {
                d = max(0, min(12, d + steps[r.nextInt(4)]))
                note(fr(d, 2), t0 + q * beat / 2, beat * 1.2, 0.22)
            }
        }
    }
    var pk = 0f
    for (x in buf) if (abs(x) > pk) pk = abs(x)
    if (pk == 0f) pk = 1f
    val bb = ByteBuffer.allocate(44 + tot * 2).order(ByteOrder.LITTLE_ENDIAN)
    bb.put("RIFF".toByteArray()); bb.putInt(36 + tot * 2); bb.put("WAVE".toByteArray())
    bb.put("fmt ".toByteArray()); bb.putInt(16); bb.putShort(1); bb.putShort(1)
    bb.putInt(sr); bb.putInt(sr * 2); bb.putShort(2); bb.putShort(16)
    bb.put("data".toByteArray()); bb.putInt(tot * 2)
    for (x in buf) bb.putShort((x / pk * 20000f).toInt().toShort())
    FileOutputStream(f).use { it.write(bb.array()) }
}

class MainActivity : Activity() {
    private val rnd = Random()
    private val h = Handler(Looper.getMainLooper())

    private var bal = 0
    private var budget = 0
    private var themeName = "Синяя"
    private var subTime = 0L
    private var bmUntil = 0L
    private val inv = ArrayList<Phone>()
    private val promos = LinkedHashMap<String, IntArray>()
    private val used = ArrayList<String>()
    private var av = ArrayList<Phone>()
    private var bl = ArrayList<Phone>()
    private var auc: Auction? = null
    private var tab = "avito"

    private lateinit var root: FrameLayout
    private lateinit var top: TextView
    private lateinit var body: FrameLayout
    private var bml: TextView? = null
    private var aucInfo: TextView? = null
    private var banner: View? = null

    private var mp: MediaPlayer? = null
    private var musicOn = false
    private var mi = 0
    private var vol = 0.5f
    private var playTok = 0

    private val tickRun = object : Runnable {
        override fun run() { tick(); h.postDelayed(this, 1000) }
    }
    private val themeRun = object : Runnable {
        override fun run() { themeTick(); h.postDelayed(this, 33) }
    }
    private val aucRun = object : Runnable {
        override fun run() { aucTick(); h.postDelayed(this, 1500) }
    }
    private val remindRun = object : Runnable {
        override fun run() { remind(); h.postDelayed(this, 25 * 60 * 1000L) }
    }

    // ---------- мелочи ----------
    private fun dp(x: Int): Int = (x * resources.displayMetrics.density + 0.5f).toInt()
    private fun now(): Long = System.currentTimeMillis()
    private fun u(a: Double, b: Double): Double = a + rnd.nextDouble() * (b - a)
    private fun r10(x: Double): Int = (Math.round(x / 10.0) * 10L).toInt()
    private fun num(x: String): Int? = x.replace(" ", "").replace("\u00A0", "").replace(",", ".").toDoubleOrNull()?.toInt()
    private fun ab(name: String, f: Cb): Pair<String, Cb> = Pair(name, f)
    private fun unlocked(): Boolean = subTime != 0L && now() - subTime >= 10000L

    private fun pm(): Double {
        return when (budget) {
            7500 -> 0.6
            10000 -> 0.8
            else -> 1.0
        }
    }

    private fun worth(p: Phone): Int {
        var s = 0.0
        for (x in p.defects) s += PEN[x] ?: 0.0
        return (p.base * max(0.15, 1.0 - s)).toInt()
    }

    private fun rcost(p: Phone, x: String): Int {
        val r = REP[x] ?: Pair(300.0, 0.06)
        return max(r.first * pm(), r.second * p.base).toInt()
    }

    private fun cond(p: Phone): String {
        val n = p.defects.size
        val lab = when {
            n == 0 -> "Отличное"
            n == 1 -> "Хорошее"
            n == 2 -> "Среднее"
            p.defects.contains("board") -> "На запчасти"
            else -> "Плохое"
        }
        return if (n == 0) "$lab — всё работает" else lab + " — " + p.defects.joinToString(", ") { NAME[it] ?: it }
    }

    private fun make(): Phone {
        val s = CAT[rnd.nextInt(CAT.size)]
        val d = ArrayList<String>()
        val pool = listOf("screen", "battery", "body", "battery", "body", "screen", "board")
        val r = rnd.nextInt(100)
        val nd = if (r < 30) 0 else if (r < 60) 1 else if (r < 85) 2 else 3
        for (i in 0 until nd) {
            val x = pool[rnd.nextInt(pool.size)]
            if (!d.contains(x)) d.add(x)
        }
        return Phone(rnd.nextLong(), s.brand, s.model, s.year, (s.price * pm()).toInt(), d, rnd.nextInt(1000000))
    }

    private fun listing(bm: Boolean): Phone {
        val p = make()
        val v = worth(p) * (if (bm) 0.8 else 1.0)
        p.ask = r10(v * u(1.0, 1.25))
        p.min = r10(v * u(0.78, 0.95))
        p.tries = 0
        return p
    }

    private fun fill() {
        av = ArrayList(List(8) { listing(false) })
        bl = ArrayList(List(6) { listing(true) })
    }

    // ---------- сохранение ----------
    private fun save() {
        val o = JSONObject()
        o.put("bal", bal); o.put("budget", budget); o.put("theme", themeName); o.put("sub", subTime); o.put("bm", bmUntil)
        val ia = JSONArray()
        for (p in inv) ia.put(p.toJson())
        o.put("inv", ia)
        val po = JSONObject()
        for ((k, v) in promos) po.put(k, JSONArray(listOf(v[0], v[1])))
        o.put("promos", po)
        o.put("used", JSONArray(used))
        getSharedPreferences("save", MODE_PRIVATE).edit().putString("s", o.toString()).apply()
    }

    private fun load() {
        promos["START"] = intArrayOf(1000, 999999)
        try {
            val s = getSharedPreferences("save", MODE_PRIVATE).getString("s", null)
            if (s != null) {
                val o = JSONObject(s)
                bal = o.optInt("bal"); budget = o.optInt("budget"); themeName = o.optString("theme", "Синяя")
                subTime = o.optLong("sub"); bmUntil = o.optLong("bm")
                val ia = o.optJSONArray("inv")
                if (ia != null) for (i in 0 until ia.length()) inv.add(Phone.fromJson(ia.getJSONObject(i)))
                val po = o.optJSONObject("promos")
                if (po != null) {
                    promos.clear()
                    val keys = po.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        val a = po.getJSONArray(k)
                        promos[k] = intArrayOf(a.getInt(0), a.getInt(1))
                    }
                }
                val ua = o.optJSONArray("used")
                if (ua != null) for (i in 0 until ua.length()) used.add(ua.getString(i))
            }
        } catch (e: Exception) {
        }
        if (themeName == "Радужная" && !unlocked()) themeName = "Синяя"
        if (!THEMES.containsKey(themeName)) themeName = "Синяя"
    }

    // ---------- виджеты ----------
    private fun bg(v: View, color: Int, r: Int = 0) {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(r).toFloat()
        v.background = g
    }

    private fun tv(t: String, sp: Float = 14f, bold: Boolean = false, color: Int = Color.WHITE,
                   gravity: Int = Gravity.CENTER_VERTICAL or Gravity.START): TextView {
        val v = TextView(this)
        v.text = t
        v.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
        v.setTextColor(color)
        if (bold) v.typeface = Typeface.DEFAULT_BOLD
        v.gravity = gravity
        return v
    }

    private fun btn(t: String, sz: Float = 13f, f: () -> Unit): Btn {
        val b = Btn(this, t, sz)
        b.setOnClickListener { f() }
        return b
    }

    private fun LinearLayout.put(v: View, hDp: Int, mb: Int = 6) {
        val lp = LinearLayout.LayoutParams(MATCH, if (hDp < 0) hDp else dp(hDp))
        lp.bottomMargin = dp(mb)
        addView(v, lp)
    }

    private fun badge(brand: String): TextView {
        val v = tv(brand, 12f, true, gravity = Gravity.CENTER)
        v.setPadding(dp(8), 0, dp(8), 0)
        v.maxLines = 1
        bg(v, BC[brand] ?: Color.rgb(102, 102, 115), 6)
        return v
    }

    private fun row(p: Phone, right: List<View>, note: String = ""): View {
        val r = LinearLayout(this)
        r.orientation = LinearLayout.HORIZONTAL
        r.setPadding(dp(6), dp(6), dp(6), dp(6))
        bg(r, CARD, 10)
        r.addView(PhoneView(this, p), LinearLayout.LayoutParams(dp(78), MATCH))
        val c = LinearLayout(this)
        c.orientation = LinearLayout.VERTICAL
        val head = LinearLayout(this)
        head.orientation = LinearLayout.HORIZONTAL
        head.gravity = Gravity.CENTER_VERTICAL
        head.addView(badge(p.brand), LinearLayout.LayoutParams(WRAP, dp(24)))
        val mt = tv("${p.model} · ${p.year}", 14f, true)
        mt.maxLines = 1
        mt.ellipsize = TextUtils.TruncateAt.END
        val mlp = LinearLayout.LayoutParams(0, MATCH, 1f)
        mlp.leftMargin = dp(6)
        head.addView(mt, mlp)
        c.addView(head, LinearLayout.LayoutParams(MATCH, dp(28)))
        c.addView(tv(cond(p) + note, 12f), LinearLayout.LayoutParams(MATCH, 0, 1f))
        val clp = LinearLayout.LayoutParams(0, MATCH, 1f)
        clp.leftMargin = dp(6)
        r.addView(c, clp)
        if (right.isNotEmpty()) {
            val b = LinearLayout(this)
            b.orientation = LinearLayout.VERTICAL
            for (x in right) {
                val lp = LinearLayout.LayoutParams(MATCH, 0, 1f)
                lp.topMargin = dp(2); lp.bottomMargin = dp(2)
                b.addView(x, lp)
            }
            val blp = LinearLayout.LayoutParams(dp(104), MATCH)
            blp.leftMargin = dp(6)
            r.addView(b, blp)
        }
        return r
    }

    private fun dlg(title: String, text: String = "", btns: List<Pair<String, Cb>> = emptyList(),
                    hint: String? = null, numeric: Boolean = false, pw: Boolean = false) {
        val d = Dialog(this)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(14), dp(12), dp(14), dp(12))
        bg(box, CARD, 14)
        val tlp = LinearLayout.LayoutParams(MATCH, WRAP)
        tlp.bottomMargin = dp(8)
        box.addView(tv(title, 18f, true), tlp)
        if (text.isNotEmpty()) {
            val xlp = LinearLayout.LayoutParams(MATCH, WRAP)
            xlp.bottomMargin = dp(8)
            box.addView(tv(text, 15f), xlp)
        }
        var ti: EditText? = null
        if (hint != null) {
            val e = EditText(this)
            e.hint = hint
            e.setSingleLine(true)
            e.setTextColor(Color.WHITE)
            e.setHintTextColor(Color.GRAY)
            e.setPadding(dp(10), dp(8), dp(10), dp(8))
            bg(e, Color.rgb(20, 24, 34), 8)
            e.inputType = if (numeric) InputType.TYPE_CLASS_NUMBER
            else if (pw) InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            else InputType.TYPE_CLASS_TEXT
            box.addView(e, LinearLayout.LayoutParams(MATCH, dp(46)))
            ti = e
        }
        val list = if (btns.isEmpty()) listOf(ab("OK") {}) else btns
        for ((name, cb) in list) {
            val blp = LinearLayout.LayoutParams(MATCH, dp(46))
            blp.topMargin = dp(8)
            box.addView(btn(name, 14f) {
                d.dismiss()
                cb(ti?.text?.toString()?.trim() ?: "")
            }, blp)
        }
        val sv = ScrollView(this)
        sv.addView(box)
        d.setContentView(sv)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        d.window?.setLayout((resources.displayMetrics.widthPixels * 0.92f).toInt(), WRAP)
        d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        d.show()
    }

    private fun info(t: String) { dlg("Сообщение", t) }

    // ---------- экран ----------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 20, 28)
        window.navigationBarColor = Color.rgb(18, 20, 28)
        load()
        Skin.color = THEMES[themeName] ?: Skin.color
        root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(18, 20, 28))
        val main = LinearLayout(this)
        main.orientation = LinearLayout.VERTICAL
        top = tv("", 17f, true, gravity = Gravity.CENTER)
        bg(top, Color.rgb(26, 31, 43))
        main.addView(top, LinearLayout.LayoutParams(MATCH, dp(46)))
        body = FrameLayout(this)
        main.addView(body, LinearLayout.LayoutParams(MATCH, 0, 1f))
        val nav = LinearLayout(this)
        nav.orientation = LinearLayout.HORIZONTAL
        for ((k, t) in TABS) {
            val lp = LinearLayout.LayoutParams(0, MATCH, 1f)
            lp.setMargins(dp(2), dp(2), dp(2), dp(2))
            nav.addView(btn(t, 12f) { show(k) }, lp)
        }
        main.addView(nav, LinearLayout.LayoutParams(MATCH, dp(56)))
        root.addView(main, FrameLayout.LayoutParams(MATCH, MATCH))
        setContentView(root)
        updTop()
        h.post(tickRun)
        h.post(themeRun)
        h.postDelayed(aucRun, 1500)
        h.postDelayed(remindRun, 25 * 60 * 1000L)
        play(rnd.nextInt(35))
        h.post { notice() }
    }

    override fun onPause() {
        super.onPause()
        save()
        try { mp?.pause() } catch (e: Exception) { }
    }

    override fun onResume() {
        super.onResume()
        if (musicOn) {
            try { mp?.start() } catch (e: Exception) { }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        h.removeCallbacksAndMessages(null)
        stopMusic()
    }

    private fun updTop() { top.text = "Баланс: " + money(bal) }

    private fun show(t: String) {
        tab = t
        bml = null
        aucInfo = null
        body.removeAllViews()
        updTop()
        val sv = ScrollView(this)
        val g = LinearLayout(this)
        g.orientation = LinearLayout.VERTICAL
        g.setPadding(dp(6), dp(6), dp(6), dp(6))
        when (t) {
            "avito" -> tAvito(g)
            "bm" -> tBm(g)
            "inv" -> tInv(g)
            "auc" -> tAuc(g)
            else -> tMore(g)
        }
        sv.addView(g)
        body.addView(sv)
    }

    private fun tick() {
        updTop()
        if (tab == "bm") {
            val left = bmUntil - now()
            if (left <= 0) {
                if (bml != null) show("bm")
            } else {
                bml?.text = "Доступ закроется через ${left / 60000}:${((left / 1000) % 60).toString().padStart(2, '0')}"
            }
        }
    }

    // ---------- Авито и чёрный рынок ----------
    private fun tAvito(g: LinearLayout) {
        g.put(btn("Обновить объявления", 14f) { fill(); show("avito") }, 46)
        for (p in av) g.put(row(p, listOf(btn(money(p.ask), 12f) { offer(p, false) })), 124)
    }

    private fun tBm(g: LinearLayout) {
        if (bmUntil - now() <= 0) {
            g.put(tv("Чёрный рынок: цены ниже на 20%, но каждая сделка с 20% шансом обмана.\nВход на 15 минут — 15 000 руб.", 15f), -2)
            g.put(btn("Войти (15 000 руб.)", 14f) { enterBm() }, 46)
            return
        }
        val l = tv("", 14f, gravity = Gravity.CENTER)
        bml = l
        g.put(l, 30)
        tick()
        for (p in bl) g.put(row(p, listOf(btn(money(p.ask), 12f) { offer(p, true) })), 124)
    }

    private fun enterBm() {
        if (bal < 15000) { info("Нужно 15 000 руб. на входной билет."); return }
        bal -= 15000
        bmUntil = now() + 15 * 60 * 1000L
        bl = ArrayList(List(6) { listing(true) })
        save()
        show("bm")
    }

    private fun offer(p: Phone, bm: Boolean) {
        dlg("${p.brand} ${p.model}", "${cond(p)}\nЦена: ${money(p.ask)}", listOf(
            ab("Купить за " + money(p.ask)) { buy(p, p.ask, bm) },
            ab("Торговаться") { haggle(p, bm) },
            ab("Назад") {}
        ))
    }

    private fun haggle(p: Phone, bm: Boolean) {
        dlg("Торг", "Сейчас просит: ${money(p.ask)}", listOf(ab("Предложить") { x ->
            val o = num(x)
            if (o != null && o > 0) {
                if (o >= p.min) {
                    buy(p, o, bm)
                } else {
                    p.tries++
                    if (p.tries >= 3) {
                        (if (bm) bl else av).remove(p)
                        info("Продавец устал торговаться и снял объявление.")
                    } else if (o >= p.min * 0.9) {
                        p.ask = r10(p.min * u(1.0, 1.04))
                        info("Продавец: «Ладно, отдам за ${money(p.ask)}»")
                    } else {
                        info("Продавец: «Это слишком мало!»")
                    }
                    show(tab)
                }
            }
        }), hint = "Ваша цена", numeric = true)
    }

    private fun buy(p: Phone, price: Int, bm: Boolean) {
        val lst = if (bm) bl else av
        if (!lst.contains(p)) return
        if (bal < price) { info("Не хватает денег."); return }
        bal -= price
        lst.remove(p)
        if (bm && rnd.nextDouble() < 0.2) {
            info("Вас обманули! Продавец пропал вместе с деньгами.")
        } else {
            p.cost = price
            inv.add(p)
            info("Куплено! Телефон на складе.")
        }
        save()
        show(tab)
    }

    // ---------- склад: ремонт и продажа ----------
    private fun tInv(g: LinearLayout) {
        if (inv.isEmpty()) {
            g.put(tv("Склад пуст. Купите телефон на Авито.", 15f), 60)
            return
        }
        for (p in inv) {
            g.put(row(p, listOf(btn("Ремонт") { repair(p) }, btn("Продать") { sell(p) }),
                "\nОценка ${money(worth(p))}\nКуплен за ${money(p.cost)}"), 124)
        }
    }

    private fun repair(p: Phone) {
        if (p.defects.isEmpty()) { info("Телефон полностью исправен."); return }
        val bt = ArrayList<Pair<String, Cb>>()
        for (x in ArrayList(p.defects)) {
            val nm = (NAME[x] ?: x).replaceFirstChar { it.uppercase() }
            bt.add(ab("$nm: ${money(rcost(p, x))}") { fix(p, x) })
        }
        bt.add(ab("Назад") {})
        dlg("Ремонт", "Что чиним?", bt)
    }

    private fun fix(p: Phone, x: String) {
        val c = rcost(p, x)
        if (bal < c) { info("Не хватает денег на ремонт."); return }
        bal -= c
        p.defects.remove(x)
        p.buyerW = 0
        save()
        show("inv")
    }

    private fun sell(p: Phone) {
        val v = worth(p)
        if (p.buyerW == 0) p.buyerW = max(1, (v * u(0.85, 1.12)).toInt())
        val w = p.buyerW
        dlg("Продажа", "Оценка рынка: ${money(v)}\nВаша цена?", listOf(ab("Выставить") { x ->
            val o = num(x)
            if (o != null && o > 0) {
                if (o <= w) {
                    sold(p, o)
                } else if (o <= w * 1.12) {
                    dlg("Покупатель", "«Скиньте до ${money(w)}?»", listOf(
                        ab("Уступить") { sold(p, w) },
                        ab("Отказать") { p.buyerW = 0; info("Покупатель ушёл.") }
                    ))
                } else {
                    p.buyerW = 0
                    info("Покупатель ушёл: слишком дорого.")
                }
            }
        }), hint = "Цена", numeric = true)
    }

    private fun sold(p: Phone, price: Int) {
        bal += price
        inv.remove(p)
        save()
        info("Продано за ${money(price)}.\nПрибыль: ${money(price - p.cost)}")
        show("inv")
    }

    // ---------- аукцион ----------
    private fun tAuc(g: LinearLayout) {
        val a = auc
        if (a == null) {
            g.put(btn("Найти новый лот", 14f) { newLot() }, 46)
            return
        }
        g.put(row(a.p, emptyList()), 124)
        val l = tv("", 15f)
        aucInfo = l
        g.put(l, 90)
        updAuc()
        g.put(btn("Ставка +3%", 14f) { bid(0.03) }, 46)
        g.put(btn("Ставка +8%", 14f) { bid(0.08) }, 46)
    }

    private fun updAuc() {
        val a = auc ?: return
        aucInfo?.text = "Ставка: ${money(a.price)}\nЛидер: ${a.lead}\nТорги закроются через ~${((4 - a.idle) * 1.5).toInt()} с без новых ставок"
    }

    private fun newLot() {
        val p = make()
        auc = Auction(p, (worth(p) * 0.45).toInt(), BOTS[rnd.nextInt(BOTS.size)], 0)
        show("auc")
    }

    private fun bid(k: Double) {
        val a = auc ?: return
        if (a.lead == "Вы") { info("Ваша ставка уже лидирует."); return }
        val n = (a.price + max(50.0, a.price * k)).toInt()
        if (bal < n) { info("Не хватает денег."); return }
        a.price = n
        a.lead = "Вы"
        a.idle = 0
        show("auc")
    }

    private fun aucTick() {
        val a = auc ?: return
        val cap = worth(a.p) * 1.05
        if (rnd.nextDouble() < 0.55 && a.price < cap) {
            a.price = (a.price + max(50.0, a.price * u(0.03, 0.08))).toInt()
            val cand = BOTS.filter { it != a.lead }
            a.lead = cand[rnd.nextInt(cand.size)]
            a.idle = 0
        } else {
            a.idle++
        }
        if (a.idle >= 4) endAuc(a) else if (tab == "auc") updAuc()
    }

    private fun endAuc(a: Auction) {
        auc = null
        if (a.lead == "Вы" && bal >= a.price) {
            bal -= a.price
            a.p.cost = a.price
            inv.add(a.p)
            save()
            info("Вы выиграли лот за ${money(a.price)}!")
        } else if (a.lead == "Вы") {
            info("Не хватило денег, лот потерян.")
        } else {
            info("Лот ушёл к ${a.lead} за ${money(a.price)}.")
        }
        if (tab == "auc" || tab == "inv") show(tab) else updTop()
    }

    // ---------- «Ещё»: промокоды, музыка, темы ----------
    private fun tMore(g: LinearLayout) {
        g.put(btn("Промокод", 14f) { promoDlg() }, 46)
        g.put(btn("Создать промокод", 14f) { mkPromo() }, 46)
        g.put(btn("Музыка", 14f) { musicDlg() }, 46)
        g.put(btn("Цвет интерфейса", 14f) { themeDlg() }, 46)
        g.put(btn("Подписаться на $CH", 14f) { subscribe() }, 46)
        g.put(btn("Новая игра", 14f) { newGame() }, 46)
        g.put(tv("ПЕРЕПРОДАЖА ТЕЛЕФОНОВ АВИТО · версия $VERSION", 12f, gravity = Gravity.CENTER), 30)
    }

    private fun promoDlg() {
        dlg("Промокод", "Введите код", listOf(ab("Активировать") { redeem(it) }), hint = "Код")
    }

    private fun redeem(x: String) {
        val c = x.uppercase()
        val pr = promos[c]
        if (pr == null) { info("Такого промокода нет."); return }
        if (used.contains(c)) { info("Вы уже использовали этот промокод."); return }
        if (pr[1] <= 0) { info("Промокод закончился."); return }
        pr[1] = pr[1] - 1
        used.add(c)
        bal += pr[0]
        save()
        updTop()
        info("Промокод активирован: +${money(pr[0])}")
    }

    private fun mkPromo() {
        val mk: Cb = { x ->
            try {
                val q = x.trim().split(Regex("\\s+"))
                val code = q[0].uppercase()
                promos[code] = intArrayOf(q[1].toInt(), q[2].toInt())
                save()
                info("Промокод $code создан.")
            } catch (e: Exception) {
                info("Неверный формат.")
            }
        }
        val chk: Cb = { x ->
            if (x != PASS) {
                info("Неверный пароль.")
            } else {
                dlg("Новый промокод", "Формат: КОД СУММА КОЛ-ВО\nНапример: GIFT 3000 5", listOf(ab("Создать", mk)), hint = "КОД СУММА КОЛ-ВО")
            }
        }
        dlg("Доступ", "Введите пароль", listOf(ab("Войти", chk)), hint = "Пароль", pw = true)
    }

    private fun newGame() {
        dlg("Новая игра", "Весь прогресс будет сброшен.", listOf(
            ab("Начать заново") {
                bal = 0; inv.clear(); bmUntil = 0L; budget = 0; auc = null
                save()
                chooseBudget()
            },
            ab("Отмена") {}
        ))
    }

    private fun themeDlg() {
        val bt = ArrayList<Pair<String, Cb>>()
        for (n in THEMES.keys) {
            val lock = if (n == "Радужная" && !unlocked()) " (подписка)" else ""
            bt.add(ab(n + lock) { pickTheme(n) })
        }
        bt.add(ab("Закрыть") {})
        dlg("Цвет интерфейса", "", bt)
    }

    private fun pickTheme(n: String) {
        if (n == "Радужная" && !unlocked()) {
            dlg("Радужная тема", "Подпишись на $CH: тема откроется через 10 секунд после нажатия кнопки.",
                listOf(ab("Подписаться") { subscribe() }))
            return
        }
        themeName = n
        save()
    }

    private fun subscribe() {
        try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(LINK))) } catch (e: Exception) { }
        if (subTime == 0L) { subTime = now(); save() }
    }

    private fun themeTick() {
        val c: Int = if (themeName == "Радужная") {
            Color.HSVToColor(floatArrayOf(((now() % 12500L) / 12500f) * 360f, 0.75f, 0.95f))
        } else {
            THEMES[themeName] ?: Skin.color
        }
        if (c != Skin.color) Skin.paint(c)
    }

    // ---------- музыка ----------
    private fun play(i: Int) {
        mi = ((i % 35) + 35) % 35
        stopMusic()
        musicOn = true
        val tok = ++playTok
        val idx = mi
        val f = File(filesDir, "t$idx.wav")
        Thread {
            try {
                if (!f.exists() || f.length() < 1000L) {
                    val tmp = File(filesDir, "t$idx.tmp")
                    synth(idx, tmp)
                    tmp.renameTo(f)
                }
            } catch (e: Exception) {
                return@Thread
            }
            runOnUiThread { startSnd(f, tok) }
        }.start()
    }

    private fun startSnd(f: File, tok: Int) {
        if (!musicOn || tok != playTok) return
        try {
            val m = MediaPlayer()
            m.setDataSource(f.absolutePath)
            m.prepare()
            m.setVolume(vol, vol)
            m.setOnCompletionListener { if (musicOn && mp === it) play(mi + 1) }
            m.start()
            mp = m
        } catch (e: Exception) {
        }
    }

    private fun stopMusic() {
        val m = mp
        mp = null
        try { m?.setOnCompletionListener(null); m?.stop() } catch (e: Exception) { }
        try { m?.release() } catch (e: Exception) { }
    }

    private fun toggle() {
        if (musicOn) { musicOn = false; stopMusic() } else play(mi)
    }

    private fun setVol(d: Float) {
        vol = min(1f, max(0f, vol + d))
        try { mp?.setVolume(vol, vol) } catch (e: Exception) { }
    }

    private fun musicDlg() {
        fun a(f: () -> Unit): Cb = { f(); musicDlg() }
        val st = if (musicOn) "играет" else "пауза"
        dlg("Музыка", "${TRK[mi]}\n$st · громкость ${(vol * 100).toInt()}%", listOf(
            ab("<< Пред.", a { play(mi - 1) }),
            ab("Играть / Пауза", a { toggle() }),
            ab("След. >>", a { play(mi + 1) }),
            ab("Громче", a { setVol(0.1f) }),
            ab("Тише", a { setVol(-0.1f) }),
            ab("Закрыть") {}
        ))
    }

    // ---------- уведомления про канал ----------
    private fun notice() {
        val d = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        d.setCancelable(false)
        val b = LinearLayout(this)
        b.orientation = LinearLayout.VERTICAL
        b.setPadding(dp(24), dp(24), dp(24), dp(24))
        b.setBackgroundColor(Color.rgb(10, 13, 20))
        b.addView(tv("Подпишись на наш Telegram-канал\n$CH\n\nНовости, промокоды и бонусы для игроков!", 21f, true, gravity = Gravity.CENTER),
            LinearLayout.LayoutParams(MATCH, 0, 1f))
        val tl = tv("Автозакрытие через 30 с", 14f, gravity = Gravity.CENTER)
        b.addView(tl, LinearLayout.LayoutParams(MATCH, dp(30)))
        val slp = LinearLayout.LayoutParams(MATCH, dp(54))
        slp.bottomMargin = dp(10)
        b.addView(btn("Подписаться", 15f) { subscribe() }, slp)
        val cb = btn("Закрыть через 5 с", 15f) { d.dismiss() }
        cb.isEnabled = false
        cb.alpha = 0.6f
        b.addView(cb, LinearLayout.LayoutParams(MATCH, dp(54)))
        d.setContentView(b)
        var n = 0
        val r = object : Runnable {
            override fun run() {
                n++
                tl.text = "Автозакрытие через ${max(0, 30 - n)} с"
                if (n < 5) {
                    cb.text = "Закрыть через ${5 - n} с"
                } else {
                    cb.text = "Закрыть"
                    cb.isEnabled = true
                    cb.alpha = 1f
                }
                if (n >= 30) d.dismiss() else h.postDelayed(this, 1000)
            }
        }
        d.setOnDismissListener { h.removeCallbacks(r); afterNotice() }
        d.show()
        h.postDelayed(r, 1000)
    }

    private fun afterNotice() {
        if (budget != 0) {
            fill()
            show("avito")
        } else {
            chooseBudget()
        }
    }

    private fun chooseBudget() {
        val d = Dialog(this)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCancelable(false)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(14), dp(14), dp(14), dp(14))
        bg(box, CARD, 14)
        val hlp = LinearLayout.LayoutParams(MATCH, WRAP)
        hlp.bottomMargin = dp(10)
        box.addView(tv("Выберите путь\nЧем меньше бюджет, тем дешевле телефоны на рынке.", 16f, true, gravity = Gravity.CENTER), hlp)
        for ((amt, t) in listOf(7500 to "Путь 1 — 7 500 руб.", 10000 to "Путь 2 — 10 000 руб.", 15000 to "Путь 3 — 15 000 руб.")) {
            val lp = LinearLayout.LayoutParams(MATCH, dp(56))
            lp.topMargin = dp(8)
            box.addView(btn(t, 15f) { d.dismiss(); start(amt) }, lp)
        }
        d.setContentView(box)
        d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        d.window?.setLayout((resources.displayMetrics.widthPixels * 0.92f).toInt(), WRAP)
        d.show()
    }

    private fun start(amt: Int) {
        bal = amt
        budget = amt
        inv.clear()
        save()
        fill()
        show("avito")
    }

    private fun remind() {
        if (banner != null) return
        val bn = LinearLayout(this)
        bn.orientation = LinearLayout.HORIZONTAL
        bn.gravity = Gravity.CENTER_VERTICAL
        bn.setPadding(dp(10), dp(6), dp(10), dp(6))
        bg(bn, Color.argb(247, 26, 31, 51))
        val lb = tv("", 13f)
        bn.addView(lb, LinearLayout.LayoutParams(0, MATCH, 1f))
        bn.addView(btn("Открыть") { subscribe() }, LinearLayout.LayoutParams(dp(100), dp(40)))
        root.addView(bn, FrameLayout.LayoutParams(MATCH, dp(64), Gravity.TOP))
        banner = bn
        var n = 10
        val r = object : Runnable {
            override fun run() {
                lb.text = "Подпишись на $CH\nЗакроется через $n с"
                if (n <= 0) {
                    root.removeView(bn)
                    banner = null
                    return
                }
                n--
                h.postDelayed(this, 1000)
            }
        }
        r.run()
    }
}

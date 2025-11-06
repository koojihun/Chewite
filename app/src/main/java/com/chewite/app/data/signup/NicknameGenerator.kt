package com.chewite.app.data.signup

import kotlin.random.Random

object NicknameGenerator {

    data class Config(
        val maxLength: Int = 20,
        val allowEmoji: Boolean = true,
        val includeNumberSuffix: Boolean = true,
        val localeSeparator: String = " ",
        val seed: Long? = null
    )

    // ---------------- 토큰 & 우선순위 ----------------
    private enum class Kind(val removableRank: Int) {
        CORE(999),          // 제거 금지(동물/핵심 food)
        PARTICLE(900),      // '의' (붙임 처리)
        ADJ(6),
        PLACE(5),
        TIME(5),
        VERB(4),            // "좋아하는", "먹는"
        ROLE(3),
        SUFFIX(2),          // "12호", "No.7"
        EMOJI(1)            // 이모지
    }

    private data class T(val text: String, val kind: Kind)

    // ---------------- 템플릿 ----------------
    // 토큰을 나열하고, 길이 초과 시 Kind의 removableRank가 낮은 것부터 제거
    private val templates: List<(WB, RNG, Config) -> List<T>> = listOf(
        { wb, r, c ->
            listOfNotNull(
                T(wb.place(r), Kind.PLACE),
                T("의", Kind.PARTICLE),
                T(wb.adj(r), Kind.ADJ),
                T(wb.animal(r), Kind.CORE),
                if (c.includeNumberSuffix) T(wb.numSuffix(r, c), Kind.SUFFIX) else null,
                if (c.allowEmoji) T(wb.emoji(r), Kind.EMOJI) else null
            )
        },
        { wb, r, c ->
            listOfNotNull(
                T(wb.adj(r), Kind.ADJ),
                T(wb.animal(r), Kind.CORE),
                T(wb.role(r), Kind.ROLE),
                if (c.includeNumberSuffix) T(wb.numSuffix(r, c), Kind.SUFFIX) else null
            )
        },
        { wb, r, c ->
            listOfNotNull(
                T(wb.place(r), Kind.PLACE),
                T("의", Kind.PARTICLE),
                T(wb.adj(r), Kind.ADJ),
                T(wb.food(r), Kind.CORE),
                T("좋아하는", Kind.VERB),
                T(wb.animal(r), Kind.CORE),
                if (c.includeNumberSuffix) T(wb.numSuffix(r, c), Kind.SUFFIX) else null
            )
        },
        { wb, r, c ->
            listOfNotNull(
                T(wb.time(r), Kind.TIME),
                T("의", Kind.PARTICLE),
                T(wb.adj(r), Kind.ADJ),
                T(wb.animal(r), Kind.CORE),
                if (c.allowEmoji) T(wb.emoji(r), Kind.EMOJI) else null
            )
        },
        { wb, r, c ->
            listOfNotNull(
                T(wb.adj(r), Kind.ADJ),
                T(wb.food(r), Kind.CORE),
                T("먹는", Kind.VERB),
                T(wb.animal(r), Kind.CORE),
                if (c.includeNumberSuffix) T(wb.numSuffix(r, c), Kind.SUFFIX) else null
            )
        }
    )

    // ---------------- 외부 API ----------------
    fun generate(config: Config = Config()): String =
        generate(RNG(config.seed), config)

    fun suggest(
        count: Int = 10,
        config: Config = Config(),
        existing: Set<String> = emptySet(),
    ): List<String> {
        val out = LinkedHashSet<String>()
        val rng = RNG(config.seed)
        var guard = 0
        while (out.size < count && guard < count * 50) {
            val n = generate(rng, config)
            if (n !in existing) out += n
            guard++
        }
        return out.toList()
    }

    // ---------------- 내부 구현 ----------------
    private fun generate(rng: RNG, config: Config): String {
        repeat(60) {
            val toks = templates.random(rng.r).invoke(WB, rng, config)
            val name = composeWithinLimit(toks, config)
            if (!containsBanned(name) && name.length in 2..config.maxLength) return name
        }
        // 초안전 fallback
        var fallback = composeWithinLimit(
            listOf(T(WB.adj(rng), Kind.ADJ), T(WB.animal(rng), Kind.CORE)),
            config
        )
        if (fallback.length < 2) fallback = "귀여운 강아지".take(config.maxLength)
        if (containsBanned(fallback)) fallback = "귀여운 강아지".take(config.maxLength)
        return fallback
    }

    // 토큰을 조립 → 길이 초과면 낮은 우선순위부터 제거 → 최종 문자열
    private fun composeWithinLimit(tokens: List<T>, config: Config): String {
        var toks = tokens.toMutableList()

        fun joinOnce(): String {
            if (toks.isEmpty()) return ""
            val out = ArrayList<String>(toks.size)
            for (t in toks) {
                if (t.kind == Kind.PARTICLE) {
                    if (out.isNotEmpty()) {
                        // 앞 단어에 붙이기: "새벽" + "의" -> "새벽의"
                        val last = out.removeAt(out.lastIndex)
                        out.add(last + t.text)
                    } // out이 비어있으면(문두) 이 PARTCLE는 건너뜀 → "의"로 시작하지 않음
                } else {
                    out.add(t.text.trim())
                }
            }
            return out.filter { it.isNotEmpty() }
                .joinToString(config.localeSeparator)
                .replace("\\s+".toRegex(), " ")
                .trim()
        }

        var s = joinOnce()
        if (s.length <= config.maxLength) return s

        // 제거 우선순위: EMOJI(1) → SUFFIX(2) → ROLE(3) → VERB(4) → PLACE/TIME(5) → ADJ(6)
        val removalOrder = listOf(Kind.EMOJI, Kind.SUFFIX, Kind.ROLE, Kind.VERB, Kind.PLACE, Kind.TIME, Kind.ADJ)

        for (k in removalOrder) {
            val idx = toks.indexOfLast { it.kind == k }  // 보통 뒤쪽부터 덜어내면 자연스러움
            if (idx >= 0) {
                toks.removeAt(idx)
                s = joinOnce()
                if (s.length <= config.maxLength) return s
                // 계속 초과하면 다음 우선순위로 계속 제거
                // 같은 종류가 여러 개인 경우를 위해 while로 반복 제거
                while (true) {
                    val idx2 = toks.indexOfLast { it.kind == k }
                    if (idx2 < 0) break
                    toks.removeAt(idx2)
                    s = joinOnce()
                    if (s.length <= config.maxLength) return s
                }
            }
        }

        // 그래도 넘치면, CORE(음식/동물)는 유지하며 남은 것 최대한 축약
        // 마지막으로 구분자 줄이기(공백→빈문자) 시도
        s = s.replace(" ", "")
        return if (s.length <= config.maxLength) s else s.take(config.maxLength) // 이 경우는 거의 없음
    }

    private fun normalize(s: String): String =
        s.replace("\\s+".toRegex(), " ").trim()

    private fun emoji(r: RNG, c: Config): String =
        if (!c.allowEmoji) "" else WB.emoji(r)

    private fun numSuffix(r: RNG, c: Config): String =
        if (!c.includeNumberSuffix) "" else WB.numSuffix(r, c)

    // 간단 금칙어(서비스 정책에 맞게 확장 권장)
    private val banned = setOf("관리자", "운영자", "admin", "운영", "official")

    private fun containsBanned(s: String): Boolean {
        val t = s.lowercase()
        return banned.any { t.contains(it.lowercase()) }
    }

    // RNG
    private class RNG(seed: Long?) { val r = (seed?.let { Random(it) } ?: Random) }

    // ---------------- 단어 뱅크 ----------------
    private object WB {
        private val ADJ = listOf(
            "귀여운","말랑한","쫀득한","든든한","통통한","바삭한","폭신한","달콤한","고소한","담백한",
            "향긋한","상큼한","진한","깔끔한","포근한","부드러운","살랑이는","영양만점","정갈한","새콤한",
            "촉촉한","싱그러운","따스한","느긋한","재빠른","용감한","호기심 많은","수줍은","장난꾸러기",
            "활짝 웃는","반짝이는","품격있는","사르르 녹는","탱글한","산뜻한","고급스러운","은은한",
            "정성 가득","상큼 폭발","황금빛","신선한","청정한","바른","프리미엄","명랑한","행복한","활기찬",
            "포동포동","똑똑한","센스있는","씩씩한","따뜻한","해맑은","정겨운","편안한","한입 가득"
        )

        private val ANIMALS = listOf(
            "강아지","고양이","리트리버","푸들","말티즈","비숑","닥스훈트","포메라니안","코기","시바",
            "러시안 블루","먼치킨","랙돌","페르시안","샴","노르웨이 숲","스핑크스","아비시니안","벵갈","코숏",
            "토끼","햄스터","기니 피그","고슴도치","앵무새","패럿","수달","여우","라쿤","물개"
        )

        private val FOODS = listOf(
            "닭 안심 간식","오리 목뼈","연어 젤리","황태 스틱","고구마 쿠키","단호박 비스킷","사과 말랭이",
            "치즈 큐브","요거트 큐브","참치 스틱","소간 트릿","칠면조 저키","양고기 저키","치킨 미트볼",
            "멸치 스낵","꿀 고구마","바나나 칩","블루베리 스낵","당근 쿠키","감자 칩","코코넛 칩","유산균 트릿",
            "연어 파우더","연어 오븐 구이","고구마 치즈볼","단호박 치즈볼","치킨 브로스"
        )

        private val ROLES = listOf(
            "간식 연구가","셰프","미식가","견생 고수","냥생 고수","테이스터","맛 평가단","쿠키 장인","훈련 도우미",
            "건강 지킴이","식단 매니저","프로 간식러","간식 소믈리에","오븐 장인"
        )

        private val PLACES = listOf(
            "산골짝","마포","성수","연남","해운대","제주","양양","남해","속초","북촌","서촌",
            "밤하늘","초원","바닷가","숲속","강가","강변","한강","한라산","산책길","펫카페","수의사 동네"
        )

        private val TIMES = listOf(
            "아침","점심","저녁","새벽","한낮","노을","황금 시간","주말","휴일","봄밤","여름밤","가을 바람","겨울 아침"
        )

        private val NUM_SUFFIX = listOf<(Int) -> String>(
            { n -> "${n}호" }, { n -> "No.$n" }, { n -> "${n}번째" }
        )

        private val EMOJI = listOf("🐶","🐱","🐾","🦴","🍖","🍗","🍪","🍎","🥕","🍠","🐟","✨","⭐","🌿","🌙")

        fun adj(r: RNG) = ADJ.random(r.r)
        fun animal(r: RNG) = ANIMALS.random(r.r)
        fun food(r: RNG) = FOODS.random(r.r)
        fun role(r: RNG) = ROLES.random(r.r)
        fun place(r: RNG) = PLACES.random(r.r)
        fun time(r: RNG) = TIMES.random(r.r)
        fun emoji(r: RNG) = EMOJI.random(r.r)

        @Suppress("UNUSED_PARAMETER")
        fun numSuffix(r: RNG, c: Config): String {
            val n = (1..99).random(r.r)
            return NUM_SUFFIX.random(r.r).invoke(n)
        }
    }
}
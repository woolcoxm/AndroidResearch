package com.researchsystem.app.data.model

data class DeepSeekRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int = 4000,
    val temperature: Double = 0.7
)

data class Message(
    val role: String,
    val content: String
)

data class DeepSeekResponse(
    val choices: List<Choice>,
    val usage: Usage?
)

data class Choice(
    val message: Message,
    val index: Int,
    val finish_reason: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class SearchRequest(
    val q: String,
    val num: Int = 10,
    val gl: String = "us",
    val hl: String = "en"
)

data class SearchResponse(
    val organic: List<OrganicResult>
)

data class OrganicResult(
    val title: String,
    val link: String,
    val snippet: String
)

data class SearchResult(
    val title: String,
    val link: String,
    val snippet: String
)

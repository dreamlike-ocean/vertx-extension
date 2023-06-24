package top.dreamlike.model

/**
 * @QueryParam
 * @PathParam
 * @HeaderParam
 * @MatrixParam 不支持
 * @CookieParam
 * @FormParam
 */
enum class ParameterType {
    BODY, COOKIE, QUERY, PATH_PARAM, HEADER, MATRIX, FORM, CONTEXT
}
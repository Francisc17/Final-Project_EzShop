package pt.ipc.estgoh.francisco_luis.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*

fun Application.configureSecurity() {

    val jwtIssuer = environment.config.property("ktor.jwt.domain").getString()
    val jwtAudience = environment.config.property("ktor.jwt.audience").getString()
    val jwtRealm = environment.config.property("ktor.jwt.realm").getString()

    authentication {
        jwt {
            realm = jwtRealm
            verifier(makeJwtVerifier(jwtIssuer, jwtAudience))
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}


fun generateToken(id: Int): String = JWT.create()
    .withSubject(id.toString())
    .withAudience("jwt-audience")
    .withIssuer("https://jwt-provider-domain/")
    .sign(algorithm)


private val algorithm = Algorithm.HMAC256("secret")
private fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier = JWT
    .require(algorithm)
    .withAudience(audience)
    .withIssuer(issuer)
    .build()

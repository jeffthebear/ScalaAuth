package core

import com.nimbusds.jose.crypto.{MACVerifier, MACSigner}

case class JwtSecret(secret : String) {
  val signer   = new MACSigner(secret)
  val verifier = new MACVerifier(secret)
}
package br.com.filacidada.utils
import org.mindrot.jbcrypt.BCrypt

/**
 * Utilitários para hash e verificação de senhas com BCrypt (jBCrypt).
 */
object PasswordUtils {
    private const val BCRYPT_ROUNDS = 12

    fun hashPassword(plainPassword: String): String {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS))
    }

    fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(plainPassword, hashedPassword)
    }
}
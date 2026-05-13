package br.com.filacidada.config

import org.koin.dsl.module
import br.com.filacidada.repositories.*
import br.com.filacidada.service.*
import br.com.filacidada.utils.*

val appModule = module {
    single { MongoConfig() }
    single { JwtConfig() }
    single { WebSocketManager() }

    single<UsuarioRepository> { UsuarioRepositoryImpl(get<MongoConfig>().usuarios) }
    single<AuditoriaRepository> { AuditoriaRepositoryImpl(get<MongoConfig>().auditorias) }
    single<FilaRepository> { FilaRepositoryImpl(get<MongoConfig>().filas) }
    single<InstituicaoRepository> { InstituicaoRepositoryImpl(get<MongoConfig>().instituicoes) }
    single<LandingPageRepository> { LandingPageRepositoryImpl(get<MongoConfig>().landingPages) }
    single<QrCodeRepository> { QrCodeRepositoryImpl(get<MongoConfig>().qrCodes) }
    single<SenhaRepository> { SenhaRepositoryImpl(get<MongoConfig>().senhas) }

    single { FileStorageService() }
    single { EmailService() }
    single { AuditoriaService(get()) }
    single { AuthService(get(), get(), get()) }
    single { UsuarioService(get(), get(), get()) }
    single { PerfilService(get()) }
    single { InstituicaoService(get(), get()) }
    single { LandingPageService(get(), get()) }
    single { QrCodeService(get(), get(), get()) }

    // Corrigido a quantidade de get() para bater com os Services:
    single { FilaService(get(), get(), get(), get()) }
    single { SenhaService(get(), get(), get(), get(), get()) }
}
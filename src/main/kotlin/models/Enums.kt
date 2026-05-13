package br.com.filacidada.models

enum class AcaoAuditoria {
    CRIAR, ATUALIZAR, DELETAR, APROVAR, REJEITAR, LOGIN, LOGOUT
}

enum class StatusInstituicao {
    PENDENTE, APROVADA, REJEITADA, INATIVA
}

enum class Papel {
    USUARIO_FINAL, ADMIN_PLATAFORMA, ADMIN_INSTITUICAO, ATENDENTE
}

enum class StatusSenha {
    AGUARDANDO, EM_ATENDIMENTO, FINALIZADA, CANCELADA, AUSENTE
}

enum class Prioridade {
    NORMAL, PREFERENCIAL, URGENTE
}
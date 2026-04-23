package com.example.prova1_api_catdog.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import retrofit2.HttpException

fun <T> safeApiCall(apiCall: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.Loading)
    try {
        val result = apiCall()
        println("✅ API Sucesso: $result")
        emit(Result.Success(result))
    } catch (e: Exception) {
        println("❌ API Erro: ${e.message}")
        e.printStackTrace()
        val error = when (e) {
            is HttpException -> Exception("Erro HTTP: ${e.code()} - ${e.message()}")
            is IOException -> Exception("Erro de rede: Verifique sua conexão")
            else -> Exception("Erro desconhecido: ${e.message}")
        }
        emit(Result.Error(error))
    }
}
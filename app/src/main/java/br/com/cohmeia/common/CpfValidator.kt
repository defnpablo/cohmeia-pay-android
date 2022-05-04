package br.com.cohmeia.common

import java.util.*

//Got this from internet
object CpfValidator {

    fun isCpfValid(cpf: String): Boolean {
        val cleanCpf = cpf.replace(".","").replace("-","")

        // considera-se erro cleanCpf's formados por uma sequencia de numeros iguais
        if (cleanCpf == "00000000000" ||
            cleanCpf == "11111111111" ||
            cleanCpf == "22222222222" || cleanCpf == "33333333333" ||
            cleanCpf == "44444444444" || cleanCpf == "55555555555" ||
            cleanCpf == "66666666666" || cleanCpf == "77777777777" ||
            cleanCpf == "88888888888" || cleanCpf == "99999999999" ||
            cleanCpf.length != 11
        )
            return false

        val dig10: Char
        val dig11: Char
        var sm: Int
        var i: Int
        var r: Int
        var num: Int
        var peso: Int

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0
            peso = 10
            i = 0
            while (i < 9) {
                // converte o i-esimo caractere do cleanCpf em um numero:
                // por exemplo, transforma o caractere '0' no inteiro 0
                // (48 eh a posicao de '0' na tabela ASCII)
                num = cleanCpf[i].toInt() - 48
                sm = sm + num * peso
                peso = peso - 1
                i++
            }

            r = 11 - sm % 11
            if (r == 10 || r == 11)
                dig10 = '0'
            else
                dig10 = (r + 48).toChar() // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0
            peso = 11
            i = 0
            while (i < 10) {
                num = cleanCpf[i].toInt() - 48
                sm = sm + num * peso
                peso = peso - 1
                i++
            }

            r = 11 - sm % 11
            if (r == 10 || r == 11)
                dig11 = '0'
            else
                dig11 = (r + 48).toChar()

            // Verifica se os digitos calculados conferem com os digitos informados.
            return if (dig10 == cleanCpf[9] && dig11 == cleanCpf[10])
                true
            else
                false
        } catch (erro: InputMismatchException) {
            return false
        }

    }

}
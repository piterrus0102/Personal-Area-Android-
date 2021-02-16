package ru.elesta.jupiter_lkselfguard.AccountFragment


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import org.jetbrains.anko.support.v4.runOnUiThread
import org.json.JSONObject
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentAccountSettings.AccountFragmentAccountSettings
import ru.elesta.jupiter_lkselfguard.AccountFragment.AccountFragmentResponsiblePersons.AccountFragmentResponsiblePersons
import ru.elesta.jupiter_lkselfguard.R
import ru.elesta.jupiter_lkselfguard.helpers.TimeConverter
import ru.elesta.jupiter_lkselfguard.httpManager.WebSocketFactory
import ru.yandex.money.android.sdk.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashSet

class AccountFragment : Fragment(), View.OnClickListener {

    lateinit var childFt: FragmentTransaction

    lateinit var contractNumberTextView2: TextView
    lateinit var contractStatusTextView2: TextView
    lateinit var contractStartDateTextView2: TextView
    lateinit var contractEndDateTextView2: TextView
    lateinit var contractBalanceTextView2: TextView
    //lateinit var fundAccountButton2: Button кнопка для баланса (Яндекс.Кассы)

    lateinit var viewOfAccount: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_account, null)
        Log.v("Account", "OnCreateView")
        viewOfAccount = v
        v.findViewById<Button>(R.id.accountSettings).setOnClickListener(this)
        v.findViewById<Button>(R.id.responsibles).setOnClickListener(this)

        contractNumberTextView2 = v.findViewById(R.id.contractNumber2)
        contractStatusTextView2 = v.findViewById(R.id.contractStatus2)
        contractStartDateTextView2 = v.findViewById(R.id.contractStartDate2)
        contractEndDateTextView2 = v.findViewById(R.id.contractEndDate2)
        contractBalanceTextView2 = v.findViewById(R.id.contractBalance2)
        //fundAccountButton2 = v.findViewById(R.id.fundAccountButton2) инициализация кнопки баланса  (Яндекс.Кассы)

        contractNumberTextView2.text = WebSocketFactory.getInstance().krosContract.number
        if(WebSocketFactory.getInstance().krosContract.status == "Active"){
            contractStatusTextView2.text = "Обслуживается"
            contractStatusTextView2.setTextColor(Color.GREEN)
        }
        if(WebSocketFactory.getInstance().krosContract.status == "Pause"){
            contractStatusTextView2.text = "Не обслуживается"
            contractStatusTextView2.setTextColor(Color.RED)
        }
        if(WebSocketFactory.getInstance().krosContract.status == "Closed"){
            contractStatusTextView2.text = "Не обслуживается"
            contractStatusTextView2.setTextColor(Color.RED)
        }
        contractStartDateTextView2.text = TimeConverter().convertContractDate(WebSocketFactory.getInstance().krosContract.dateStart)
        contractEndDateTextView2.text = TimeConverter().convertContractDate(WebSocketFactory.getInstance().krosContract.dateEnd)
        contractBalanceTextView2.text = "0"

        //fundAccountButton2.setOnClickListener(this) листенер для кнопки баланса (Яндекс кассы)

        return v
    }

    fun timeToStartCheckout() {
        val paymentTypes = HashSet<PaymentMethodType>()
        paymentTypes.add(PaymentMethodType.BANK_CARD)
        /*paymentTypes.add(PaymentMethodType.SBERBANK)
        paymentTypes.add(PaymentMethodType.GOOGLE_PAY)*/
        val intent = Checkout.createTokenizeIntent(activity!!.baseContext, paymentParameters =  PaymentParameters(
            Amount(BigDecimal(200), Currency.getInstance("RUB")),
            "Охрана",
            "Тревожная кнопка",
            "test_NjUzNjg2FyIbJUJlr37xBHQPuQJcfSeBASUzm7jRCRY", // ТЕСТОВЫЙ МАГАЗИН ЭЛЕСТЫ
            "653686",
            SavePaymentMethod.OFF,
            paymentMethodTypes = paymentTypes
        )
        )
        startActivityForResult(intent, 111)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("requestCode", requestCode.toString())
        Log.v("resultCode", resultCode.toString())
        if (requestCode == 111) {
            if (resultCode == Activity.RESULT_OK) {
                // successful tokenization
                val result = Checkout.createTokenizationResult(data!!)
                runOnUiThread {
                    Toast.makeText(activity, result.paymentToken, Toast.LENGTH_LONG).show()
                    //tokenTextView.text = result.paymentToken
                }
                Log.v("ResultCODE", "RESULT_OK")
                Log.v("Result", result.toString())
            }
            if(resultCode == Activity.RESULT_CANCELED){
                // user canceled tokenization
                Log.v("Result", "RESULT_CANCELED")
            }
        }
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.responsibles -> {
                WebSocketFactory.getInstance().setOfResponsibles.clear()
                val message4 = JSONObject()
                    .put("action","get")
                    .put("result", JSONObject().put("responsibles",""))
                var m = ""
                for(i in WebSocketFactory.getInstance().licenseContract.services){
                    if(i.serviceID == "1"){
                        m = i.id
                    }
                }
                WebSocketFactory.getInstance().mapSockets.get(m)?.send(message4.toString())
                val responsiblePersons = AccountFragmentResponsiblePersons()
                childFt = activity?.supportFragmentManager!!.beginTransaction()
                childFt.replace(R.id.fragmentContainer, responsiblePersons)
                childFt.addToBackStack(null)
                childFt.commit()
            }
            R.id.accountSettings -> {
                val accountSettings = AccountFragmentAccountSettings()
                childFt = activity?.supportFragmentManager!!.beginTransaction()
                childFt.replace(R.id.fragmentContainer, accountSettings)
                childFt.addToBackStack(null)
                childFt.commit()
            }
//            R.id.fundAccountButton2 -> {
//                timeToStartCheckout() функция обработки платежей яндекс Касс
//            }
        }
    }
}

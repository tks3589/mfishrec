package com.aaron.mfishrec.lib

import android.content.Context
import android.location.Location
import android.util.Log
import com.aaron.mfishrec.model.PriceModel

class GpsMarketUtil {
    companion object{
        private val marketLocation = hashMapOf(
            "基隆" to Location("基隆"),
            "頭城" to  Location("頭城"),
            "蘇澳" to  Location("蘇澳"),
            "台南" to  Location("台南"),
            "興達港" to  Location("興達港"),
            "高雄" to  Location("高雄"),
            "梓官" to  Location("梓官"),
            "澎湖" to  Location("澎湖"),
            "東港" to  Location("東港"),
            "新港" to  Location("新港"),
            "花蓮" to  Location("花蓮"),
            "台北" to  Location("台北"),
            "三重" to  Location("三重"),
            "新竹" to  Location("新竹"),
            "桃園" to  Location("桃園"),
            "苗栗" to  Location("苗栗"),
            "台中" to  Location("台中"),
            "彰化" to  Location("彰化"),
            "埔心" to  Location("埔心"),
            "埔里" to  Location("埔里"),
            "嘉義" to  Location("嘉義"),
            "斗南" to  Location("斗南"),
            "佳里" to  Location("佳里"),
            "新營" to  Location("新營"),
            "岡山" to  Location("岡山")
        )
        fun getClosestMarket(context: Context,fishPrice:ArrayList<PriceModel>,location: Location):Array<Any>{
            var closesMarket = fishPrice[0].market
            if(location == null)
                return arrayOf(0,closesMarket)
            initMarketPosition()
            var closestDistance = Float.MAX_VALUE
            var closestIndex = 0
            var newestDate = fishPrice[0].date
            var index = 0
            fishPrice.forEach {
                if(it.date!=newestDate)
                    return@forEach
                //Log.d("GPS",it.toString())
                //Log.d("GPS","lat:${current.latitude},lng:${current.longitude}")
                var tmpDistance = location.distanceTo(marketLocation[it.market])
                if (tmpDistance < closestDistance) {
                    closestDistance = tmpDistance
                    closesMarket = it.market
                    closestIndex = index
                }
                Log.d("gps","$index,${it}")
                index+=1
            }
            Log.d("gps","summmm: $closestIndex,$closesMarket")
            return arrayOf(closestIndex,closesMarket)
        }

        private fun initMarketPosition(){
            marketLocation.forEach {(market,location) ->
                when(market){
                    "基隆" -> {
                        location.latitude = 25.1241862
                        location.longitude = 121.6475837
                    }
                    "頭城" -> {
                        location.latitude = 24.8491092
                        location.longitude = 121.7924881
                    }
                    "蘇澳" -> {
                        location.latitude = 24.5436326
                        location.longitude = 121.7625844
                    }
                    "台南" -> {
                        location.latitude = 23.1229948
                        location.longitude = 120.1312989
                    }
                    "興達港" -> {
                        location.latitude = 22.8623605
                        location.longitude = 120.1888611
                    }
                    "高雄" -> {
                        location.latitude = 21.9931016
                        location.longitude = 117.7364923
                    }
                    "梓官" -> {
                        location.latitude = 22.7441583
                        location.longitude = 120.2421038
                    }
                    "澎湖" -> {
                        location.latitude = 23.5202913
                        location.longitude = 119.2417149
                    }
                    "東港" -> {
                        location.latitude = 22.4653276
                        location.longitude = 120.4344364
                    }
                    "新港" -> {
                        location.latitude = 23.5491302
                        location.longitude = 120.3174685
                    }
                    "花蓮" -> {
                        location.latitude = 23.9943971
                        location.longitude = 121.5674181
                    }
                    "台北" -> {
                        location.latitude = 25.0174719
                        location.longitude = 121.3662927
                    }
                    "三重" -> {
                        location.latitude = 25.0666603
                        location.longitude = 121.4685643
                    }
                    "新竹" -> {
                        location.latitude = 24.7835529
                        location.longitude = 120.9316641
                    }
                    "桃園" -> {
                        location.latitude = 24.8551722
                        location.longitude = 120.9519953
                    }
                    "苗栗" -> {
                        location.latitude = 24.5151718
                        location.longitude = 120.6615398
                    }
                    "台中" -> {
                        location.latitude = 24.2204731
                        location.longitude = 120.6756843
                    }
                    "彰化" -> {
                        location.latitude = 23.992187
                        location.longitude = 120.3230676
                    }
                    "埔心" -> {
                        location.latitude = 23.9542163
                        location.longitude = 120.5193019
                    }
                    "埔里" -> {
                        location.latitude = 23.9791657
                        location.longitude = 120.9039052
                    }
                    "嘉義" -> {
                        location.latitude = 23.4257436
                        location.longitude = 120.2573551
                    }
                    "斗南" -> {
                        location.latitude = 23.6706261
                        location.longitude = 120.4440291
                    }
                    "佳里" -> {
                        location.latitude = 23.164872
                        location.longitude = 120.1453357
                    }
                    "新營" -> {
                        location.latitude = 23.2997554
                        location.longitude = 120.2658766
                    }
                    "岡山" -> {
                        location.latitude = 22.804028
                        location.longitude = 120.2666237
                    }
                }
            }
        }
    }
}
/*
台北  三重  新竹  桃園  苗栗  台中  彰化  埔心  埔里  嘉義
斗南  佳里  新營  岡山

基隆  頭城  蘇澳  台南  興達港  高雄  梓官  澎湖  東港  新港
花蓮
*/
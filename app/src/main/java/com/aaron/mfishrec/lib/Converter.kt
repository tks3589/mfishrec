package com.aaron.mfishrec.lib

import com.aaron.mfishrec.model.GuideModel
import com.aaron.mfishrec.model.MenuModel
import com.aaron.mfishrec.model.PriceModel

class Converter {
    companion object{
        fun convertGuide(map: Map<String,Any>):GuideModel{
            val id = map["id"] as Long
            val name = map["name"] as String
            val imgurl = map["imgurl"] as String
            val description = map["description"] as String
            return GuideModel(id,name,imgurl, description)
        }
        fun convertPrice(map: Map<String,Any>):ArrayList<PriceModel>{
            var sortedMap = map.toSortedMap(compareByDescending { it })
            var data = arrayListOf<PriceModel>()
            sortedMap.forEach {(date,allValue) ->
                //Log.d("guideAdapter","$date : $allValue")
                val marketValue = allValue as Map<String,Any>
                marketValue.forEach{(market,fishAll) ->
                    //Log.d("guideAdapter","$date : $market : $fishAll")
                    val fishAllValue = fishAll as Map<String,Any>
                    fishAllValue.forEach{(fish,fishDetail) ->
                        //Log.d("guideAdapter","$date : $market : $fish : $fishDetail")
                        val fishDetailValue = fishDetail as Map<String,Any>
                        val id = fishDetailValue.get("id") as Long
                        val up = fishDetailValue.get("up") as Double
                        val mid = fishDetailValue.get("mid") as Double
                        val down = fishDetailValue.get("down") as Double
                        val avg = fishDetailValue.get("avg") as Double
                        val count = fishDetailValue.get("count") as Double
                        data.add(PriceModel(date,market,fish,id,up,mid,down,avg,count))
                    }
                }
            }
            return data
        }
        fun convertMenu(map: Map<String,Any>):ArrayList<MenuModel>{
            var sortedMap = map.toSortedMap(compareByDescending { it })
            var data = arrayListOf<MenuModel>()
            sortedMap.forEach { (name,allValue) ->
                //Log.d("converter","$name : $allValue")
                val cookValue = allValue as Map<String,Any>
                val imgurl = cookValue["imgurl"] as String
                val materials = cookValue["materials"] as ArrayList<String>
                val steps = cookValue["steps"] as ArrayList<String>
                data.add(MenuModel(name, imgurl, materials, steps))
            }
            //Log.d("converter","$data")
            return data
        }
    }
}
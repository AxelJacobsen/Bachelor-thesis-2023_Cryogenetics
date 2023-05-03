package cryogenetics.logistics.ui.actLog.functions

class Functions {
    companion object {
        /**
         * Updates Temp number to also carry container size
         *
         * Iterates data provided and returns an updated List containing corrected number
         *
         * @param urlString the complete url to query
         * @return the json data in string format, send directly to parseJson
         */
        fun enforceNumberFormat(inData: Map<String, Any>): Map<String, Any> {
            var editMap = mutableMapOf<String,Any>()
            editMap = inData as MutableMap<String, Any>
            val fieldNames = listOf<String>("id", "liter_capacity")
            editMap[fieldNames[0]] =
                    (inData[fieldNames[1]].toString() +
                    "-" +
                    addZeros(inData[fieldNames[0]].toString()))
            return editMap
        }

        /**
         * Adds zeros in front of temp number to enforce uniformity
         *
         * Pretty dumb and inefficient but it gets the job done by checking if length of number is
         * smaller than the desired number of characters, if not adds more zeros in front
         *
         * @param inString id from json data
         * @return id but withe more zeros
         */
        fun addZeros(inString: String): String{
            val totalChars = 4
            var outString = ""
            if (inString.length < totalChars){
                var i = 0
                while (i < (totalChars-inString.length)){
                    outString += "0"
                    i++
                }
                outString += inString
            } else {
                return inString
            }
            return outString
        }
    }
}
package cryogenetics.logistics.functions

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

            val fieldNames = listOf<String>("id", "liter_capacity")
            print(inData[fieldNames[0]])
            inData[fieldNames[0]] to
                    (inData[fieldNames[1]].toString() +
                    "-" +
                    inData[fieldNames[0]].toString())
            print(inData[fieldNames[0]])
            return inData
        }
    }
}
export const productLocalNameFilter = function (vue) {
    vue.filter('localeProductName', function () {


        var localeForEach;

        var localeMatch = function (item) {
            return item.value.slice(-5) === localeForEach;
        };

        return function (productNameList, locale) {
            console.log("locale" + locale)
            console.log("productNameList" ,productNameList)
            localeForEach = locale;
            var results = [];
            if (!productNameList) {
                return;
            }

            var mapNameList = {entry: []};
            _.each(productNameList, function (country, name) {
                mapNameList.entry.push({key: name, value: country});
            });
            var entrys = _.filter(mapNameList.entry, function(name){
                return name.key !== undefined;
            });
            var itemGroups = _.groupBy(entrys, function (item) {
                return item.value.substr(0, item.value.length - 5);
            });
            for (var key in itemGroups) {
                var itemGroup = itemGroups[key];
                if (itemGroup.length === 1) {
                    results.push(itemGroup[0].key);
                } else {
                    var temp = _.find(itemGroup, localeMatch);
                    if (temp !== undefined) {
                        results.push(temp.key);
                    } else {
                        var temp1 = itemGroup[0];
                        results.push(temp1.key);
                    }
                }
            }


            return results;
        };
    });
}


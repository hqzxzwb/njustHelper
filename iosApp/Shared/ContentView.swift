//
//  ContentView.swift
//  Shared
//
//  Created by zhuwenbo on 2022/1/26.
//

import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        let a = callApi()
        Text(Greeting().greeting())
            .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

func callApi() {
    let cancellable = JwcApi.shared.gradeLevelIos(stuid: "", pwd: "") { result, error in
        print(result)
        print(error)
    }
    cancellable.cancel()
    print("cancelled")
}

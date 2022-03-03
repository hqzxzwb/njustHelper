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
    let job = LinksApi.shared.linksNative()
        .subscribe { result in
            print(result)
        } onThrow: { error in
            print(error)
        }
    job.cancel(cause: nil)
    print("cancelled")
}

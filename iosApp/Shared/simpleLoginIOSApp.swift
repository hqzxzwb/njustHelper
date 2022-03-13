//
//  simpleLoginIOSApp.swift
//  Shared
//
//  Created by zhuwenbo on 2022/1/26.
//

import SwiftUI
import shared

@main
struct simpleLoginIOSApp: App {
    init() {
        let configuration = IosConfiguration()
        PlatformKt.startIos(configuration: configuration)
    }
    
    var body: some Scene {
        WindowGroup {
            LinksScreenView()
        }
    }
}

//
//  ContentView.swift
//  Shared
//
//  Created by zhuwenbo on 2022/1/26.
//

import SwiftUI
import shared

struct LinksScreenView: View {
    @ObservedObject var vm: LinksViewModelImpl = LinksViewModelImpl()
    let model: LinksModel

    init() {
        let vm = LinksViewModelImpl()
        self.vm = vm
        self.model = LinksModel(vm: vm)
    }

    var body: some View {
        NavigationView {
            List(vm.items, id: \.self) { link in
                LinkRow(link: link)
            }
            .navigationTitle("常用链接")
            .navigationBarTitleDisplayMode(.inline)
        }
        .onAppear(perform: {
            model.collectEventsNative()
                .subscribe(onSuccess: {_ in}, onThrow: {_ in})
            model.loadNative()
                .subscribe(onSuccess:{_ in}, onThrow: {_ in})
        })
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        LinksScreenView()
    }
}

struct LinkRow: View {
    var link: CommonLink

    var body: some View {
        Button(link.name) {
            
        }
    }
}

class LinksViewModelImpl: LinksViewModel, ObservableObject {
    @Published private var _loading = false
    override var loading: Bool {
        get { _loading }
        set { _loading = newValue }
    }
    @Published private var _items: [CommonLink] = []
    override var items: [CommonLink] {
        get { _items }
        set { _items = newValue }
    }
}

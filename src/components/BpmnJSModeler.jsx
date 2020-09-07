import React from 'react';
import BpmnModeler from "bpmn-js/lib/Modeler";
import propertiesPanelModule from 'bpmn-js-properties-panel';
import propertiesProviderModule from 'bpmn-js-properties-panel/lib/provider/camunda';
import camundaModdleDescriptor from 'camunda-bpmn-moddle/resources/camunda.json';

import "diagram-js/assets/diagram-js.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css";
import "bpmn-js-properties-panel/dist/assets/bpmn-js-properties-panel.css";
import "./styles.css";

const initialDiagram =
  '<?xml version="1.0" encoding="UTF-8"?>' +
  '<bpmn:definitions xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" ' +
  'xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" ' +
  'xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" ' +
  'xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" ' +
  'targetNamespace="http://bpmn.io/schema/bpmn" ' +
  'id="Definitions_1">' +
  '<bpmn:process id="Process_1" isExecutable="false">' +
  '<bpmn:startEvent id="StartEvent_1"/>' +
  '</bpmn:process>' +
  '<bpmndi:BPMNDiagram id="BPMNDiagram_1">' +
  '<bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">' +
  '<bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">' +
  '<dc:Bounds height="36.0" width="36.0" x="173.0" y="102.0"/>' +
  '</bpmndi:BPMNShape>' +
  '</bpmndi:BPMNPlane>' +
  '</bpmndi:BPMNDiagram>' +
  '</bpmn:definitions>';


class BpmnJSModeler extends React.Component {

  constructor(props) {
    super(props);
    this.canvas = null;
    this.modeler = null;
    this.fileSelect = null;
    this.state = { xmlDownloadLink: '#', svgDownloadLink: '#' };
  }

  componentDidMount() {
    const modeler = new BpmnModeler({
      container: this.canvas,
      propertiesPanel: {
        parent: '#js-properties-panel'
      },
      additionalModules: [
        propertiesPanelModule,
        propertiesProviderModule
      ],
      moddleExtensions: {
        camunda: camundaModdleDescriptor
      }
    });
    this.modeler = modeler;
    this.createNewDiagram();
  }

  createNewDiagram() {
    this.openDiagram(initialDiagram);
  }

  async openDiagram(xml) {
    await this.modeler.importXML(xml).catch(e => {
      alert("Ooops, we could not display that diagram.")
    });
  }

  async saveXML() {
    const { xml } = await this.modeler.saveXML({ format: true });
    const href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(xml);
    this.setState({ xmlDownloadLink: href })
  }

  async saveSVG() {
    const { svg } = await this.modeler.saveSVG();
    const href = 'data:application/bpmn20-xml;charset=UTF-8,' + encodeURIComponent(svg);
    this.setState({ svgDownloadLink: href })
  }

  handleFileSelect(e, callback) {
    e.stopPropagation();
    e.preventDefault();
    var reader = new FileReader();
    reader.onload = function (e) {
      var xml = e.target.result;
      callback(xml);
    };
    reader.readAsText(e.target.files[0]);
  }

  render() {
    return (
      <div className="bpmn-js-content bpmn-js-with-diagram" id="js-drop-zone">
        <div className="bpmn-js-canvas" id="js-canvas" ref={e => this.canvas = e} />
        <div className="properties-panel-parent" id="js-properties-panel" />
        <div className="bpmn-js-buttons">
          <ul>
            <li><button onClick={e => this.fileSelect.click()}>Open</button></li>
            <li><button onClick={() => this.openDiagram(initialDiagram)}>Create</button></li>
            <li>
              <button onClick={() => this.saveXML()} >
                <a href={this.state.xmlDownloadLink} download="diagram.bpmn">
                  Download BPMN
                                </a>
              </button>
            </li>
            <li>
              <button onClick={() => this.saveSVG()} >
                <a href={this.state.svgDownloadLink} download="diagram.svg">
                  Download SVG
                                </a>
              </button>
            </li>
          </ul>
        </div>
        <input onChange={(e) => this.handleFileSelect(e, (xml) => this.openDiagram(xml))} type="file" id="bpmn-file" style={{ display: 'none' }} ref={e => this.fileSelect = e} />
      </div>
    )
  }

}

export default BpmnJSModeler;
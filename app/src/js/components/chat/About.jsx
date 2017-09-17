import React from 'react';
import Modal from 'react-modal';

class About extends React.Component {
    getUrlParam(name) {
        return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(name).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
    }

    constructor(props) {
        super(props);

        this.onCloseClick = this.onCloseClick.bind(this);
    }

    onCloseClick(event) {
        event.preventDefault();
        if(this.props.hide) {
            this.props.hide();
        }
    }

    render() {
        return (
            <div>
                <Modal
                    isOpen={this.props.isOpen}
                    contentLabel="About Modal"
                    style={{content: {border: 'none', background: 'none', top: 20, left: 20, right: 20, bottom: 20, zIndex: 10}}}>

                    <div className="card feedback-container slideDown">
                        <form>
                            <fieldset>
                                <legend>DATENSCHUTZRICHTLINIE</legend>
                                Datenschutzerklärung

                                Diese App ist ein Service der DBpedia Association.

                                Für externe Links zu fremden Inhalten können wir dabei trotz sorgfältiger inhaltlicher Kontrolle keine Haftung übernehmen. Für den Inhalt der verlinkten Seiten sind ausschließlich deren Betreiber verantwortlich.

                                Jeder Zugriff unserer App auf Internetseiten und Webservices dient dazu, Inhalte für die App bereit zu stellen bzw. diese zu aktualisieren. Weitergehende personenbezogene Daten werden nur erfasst, wenn Sie diese Angaben freiwillig, etwa im Rahmen einer Anfrage, einer Antwort oder Registrierung, machen. Zugriffe auf die App und jeder Abruf einer auf der Homepage hinterlegten Datei werden protokolliert. Die Speicherung dient internen systembezogenen, wissenschaftlichen und statistischen Zwecken oder zur Optimierung unserer Apps und Services. Ihre Daten werden dabei nicht an Dritte weitergegeben.

                                Für Anfragen wenden Sie sich bitte an die DBpedia Association.

                                <legend>Privacy Policy</legend>
                                The responsibility involved with handling of personal information is important to us. Your personal data will only be used in conjunction with the use of the platform.

                                We do not collect any data to identify you. We raise information to optimize the quality of our apps and services and to do research.

                                Requests to our websites and webservices are required to provide actual content and updates to you. All data input from your side are optional.

                                For questions please refer to the DBpedia association.

                                <h5>DBpedia Projects</h5>
                                <ol>
                                    <li>AKSW Genesis</li>
                                    <li>QANARY (WD Aqua)</li>
                                    <li>DBpedia Lookup</li>
                                    <li>DBpedia Spotlight</li>
                                </ol>

                                <h5>Chat Libraries</h5>
                                <ol>
                                    <li>RiveScript</li>
                                    <li>Eliza</li>
                                </ol>

                                <h5>Platform Libraries</h5>
                                <ol>
                                    <li>Messenger4j</li>
                                    <li>JSlack</li>
                                </ol>

                                <h5>External APIs</h5>
                                <ol>
                                    <li>Wolfram Alpha</li>
                                    <li>Nominatim API</li>
                                    <li>TMDB API</li>
                                </ol>

                                <h5>General Libraries</h5>
                                <ol>
                                    <li>Apache Jena</li>
                                    <li>JLanguage Tool</li>
                                    <li>Jackson</li>
                                    <li>GSON</li>
                                    <li>jUnit</li>
                                    <li>Google jUnit Toolbox</li>
                                </ol>

                                <h5>Front Libraries/Frameworks</h5>
                                <ol>
                                    <li>React.js</li>
                                    <li>Twitter Bootstrap</li>
                                    <li>Bootstrap Material Design</li>
                                    <li>LESS</li>
                                </ol>
                                <h5>Backend Libraries/Frameworks</h5>
                                <ol>
                                    <li>Spring</li>
                                    <li>Cloudant</li>
                                </ol>

                                <div className="form-group">
                                    <button onClick={this.onCloseClick} className="btn btn-raised btn-primary">Close</button>
                                </div>
                            </fieldset>

                        </form>
                    </div>
                </Modal>
            </div>
        )
    }
}

About.defaultProps = {

};
export default About;